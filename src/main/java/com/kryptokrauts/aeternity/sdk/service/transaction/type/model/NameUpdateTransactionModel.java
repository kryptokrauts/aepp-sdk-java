package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.GenericTx;
import com.kryptokrauts.aeternity.generated.model.NamePointer;
import com.kryptokrauts.aeternity.generated.model.NameUpdateTx;
import com.kryptokrauts.aeternity.sdk.constants.ApiIdentifiers;
import com.kryptokrauts.aeternity.sdk.exception.InvalidParameterException;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.NameUpdateTransaction;
import com.kryptokrauts.aeternity.sdk.util.ValidationUtil;
import com.kryptokrauts.sophia.compiler.generated.api.rxjava.DefaultApi;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
public class NameUpdateTransactionModel extends AbstractTransactionModel<NameUpdateTx> {

  public static String POINTER_KEY_ACCOUNT = "account_pubkey";
  public static String POINTER_KEY_CHANNEL = "channel";
  public static String POINTER_KEY_CONTRACT = "contract_pubkey";
  public static String POINTER_KEY_ORACLE = "oracle_pubkey";

  private static Map<String, String> identifierToPointerKeyMap;

  {
    identifierToPointerKeyMap =
        new HashMap<String, String>() {
          {
            put(ApiIdentifiers.ACCOUNT_PUBKEY, POINTER_KEY_ACCOUNT);
            put(ApiIdentifiers.CHANNEL, POINTER_KEY_CHANNEL);
            put(ApiIdentifiers.CONTRACT_PUBKEY, POINTER_KEY_CONTRACT);
            put(ApiIdentifiers.ORACLE_PUBKEY, POINTER_KEY_ORACLE);
          }
        };
  }

  private String accountId;
  private BigInteger nonce;
  private String nameId;
  private BigInteger ttl;
  private BigInteger nameTtl;
  private BigInteger clientTtl;

  @Default private List<String> pointerAddresses = new LinkedList<>();

  @Override
  public NameUpdateTx toApiModel() {
    NameUpdateTx nameUpdateTx = new NameUpdateTx();
    nameUpdateTx.setAccountId(this.accountId);
    nameUpdateTx.setNonce(this.nonce);
    nameUpdateTx.setNameId(nameId);
    nameUpdateTx.setFee(this.fee);
    nameUpdateTx.setTtl(this.ttl);
    nameUpdateTx.setNameTtl(nameTtl);
    nameUpdateTx.setClientTtl(clientTtl);
    nameUpdateTx.setPointers(getGeneratedPointers());
    return nameUpdateTx;
  }

  public List<NamePointer> getGeneratedPointers() {
    return pointerAddresses.stream()
        .map(pointerAddress -> buildNamePointer(pointerAddress))
        .collect(Collectors.toList());
  }

  @Override
  public Function<GenericTx, NameUpdateTransactionModel> getApiToModelFunction() {
    return (tx) -> {
      NameUpdateTx castedTx = (NameUpdateTx) tx;
      return this.toBuilder()
          .accountId(castedTx.getAccountId())
          .fee(castedTx.getFee())
          .nonce(castedTx.getNonce())
          .nameId(castedTx.getNameId())
          .nameTtl(castedTx.getNameTtl())
          .clientTtl(castedTx.getClientTtl())
          .ttl(castedTx.getTtl())
          .pointerAddresses(
              castedTx.getPointers().stream()
                  .map(pointer -> pointer.getId())
                  .collect(Collectors.toList()))
          .build();
    };
  }

  @Override
  public void validateInput() {
    // Validate parameters
    ValidationUtil.checkParameters(
        validate -> Optional.ofNullable(nameId != null),
        nameId,
        "validateUpdateTransaction",
        Arrays.asList("nameId"),
        ValidationUtil.PARAMETER_IS_NULL);
    ValidationUtil.checkParameters(
        validate -> Optional.ofNullable(nameId.startsWith(ApiIdentifiers.NAME)),
        nameId,
        "validateUpdateTransaction",
        Arrays.asList("nameId", ApiIdentifiers.NAME),
        ValidationUtil.MISSING_API_IDENTIFIER);
    ValidationUtil.checkParameters(
        validate -> Optional.ofNullable(areDistinctPointerKeys(pointerAddresses)),
        pointerAddresses,
        "validateUpdateTransaction",
        Stream.concat(Arrays.asList("pointerAddresses").stream(), pointerAddresses.stream())
            .collect(Collectors.toList()),
        ValidationUtil.DUPLICATE_POINTER_KEY);
    for (String pointerAddress : pointerAddresses) {
      ValidationUtil.checkParameters(
          validate -> Optional.ofNullable(isValidPointerAddress(pointerAddress)),
          pointerAddress,
          "validateUpdateTransaction",
          Arrays.asList("pointerAddress", pointerAddress),
          ValidationUtil.INVALID_POINTER_ADDRESS);
    }
  }

  @Override
  public AbstractTransaction<?> buildTransaction(ExternalApi externalApi, DefaultApi compilerApi) {
    return NameUpdateTransaction.builder().externalApi(externalApi).model(this).build();
  }

  private boolean areDistinctPointerKeys(List<String> pointerAddresses) {
    return pointerAddresses.stream().map(p -> getIdentifier(p)).distinct().count()
        == pointerAddresses.size();
  }

  private boolean isValidPointerAddress(String pointerAddress) {
    return identifierToPointerKeyMap.keySet().contains(getIdentifier(pointerAddress));
  }

  private NamePointer buildNamePointer(String pointerAddress) {
    return new NamePointer()
        .key(identifierToPointerKeyMap.get(getIdentifier(pointerAddress)))
        .id(pointerAddress);
  }

  private String getIdentifier(String pointerAddress) {
    if (pointerAddress == null) {
      throw new InvalidParameterException("pointerAddress mustn't be null");
    }
    return pointerAddress.split("_")[0];
  }
}
