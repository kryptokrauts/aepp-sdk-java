package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.GenericTx;
import com.kryptokrauts.aeternity.generated.model.NamePointer;
import com.kryptokrauts.aeternity.generated.model.NameUpdateTx;
import com.kryptokrauts.aeternity.sdk.annotations.Mandatory;
import com.kryptokrauts.aeternity.sdk.constants.AENS;
import com.kryptokrauts.aeternity.sdk.constants.ApiIdentifiers;
import com.kryptokrauts.aeternity.sdk.exception.InvalidParameterException;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.NameUpdateTransaction;
import com.kryptokrauts.aeternity.sdk.util.ValidationUtil;
import com.kryptokrauts.sophia.compiler.generated.api.rxjava.DefaultApi;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@ToString
public class NameUpdateTransactionModel extends AbstractTransactionModel<NameUpdateTx> {

  @Mandatory private String accountId;
  @Mandatory private BigInteger nonce;
  @Mandatory private String nameId;
  @Mandatory private BigInteger ttl;

  private BigInteger nameTtl;
  private BigInteger clientTtl;

  @Default private List<String> pointers = new LinkedList<>();

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
    return pointers.stream().map(pointer -> buildNamePointer(pointer)).collect(Collectors.toList());
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
          .pointers(
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
        validate -> Optional.ofNullable(areDistinctPointerKeys(pointers)),
        pointers,
        "validateUpdateTransaction",
        Stream.concat(Arrays.asList("pointers").stream(), pointers.stream())
            .collect(Collectors.toList()),
        ValidationUtil.DUPLICATE_POINTER_KEY);
    for (String pointer : pointers) {
      ValidationUtil.checkParameters(
          validate -> Optional.ofNullable(isValidPointer(pointer)),
          pointer,
          "validateUpdateTransaction",
          Arrays.asList("pointer", pointer),
          ValidationUtil.INVALID_POINTER);
    }
  }

  @Override
  public AbstractTransaction<?> buildTransaction(ExternalApi externalApi, DefaultApi compilerApi) {
    return NameUpdateTransaction.builder().externalApi(externalApi).model(this).build();
  }

  private boolean areDistinctPointerKeys(List<String> pointers) {
    return pointers.stream().map(p -> getIdentifier(p)).distinct().count() == pointers.size();
  }

  private boolean isValidPointer(String pointer) {
    return AENS.IDENTIFIER_TO_POINTERKEY_MAP.keySet().contains(getIdentifier(pointer));
  }

  private NamePointer buildNamePointer(String pointer) {
    return new NamePointer()
        .key(AENS.IDENTIFIER_TO_POINTERKEY_MAP.get(getIdentifier(pointer)))
        .id(pointer);
  }

  private String getIdentifier(String pointer) {
    if (pointer == null) {
      throw new InvalidParameterException("pointer mustn't be null");
    }
    return pointer.split("_")[0];
  }
}
