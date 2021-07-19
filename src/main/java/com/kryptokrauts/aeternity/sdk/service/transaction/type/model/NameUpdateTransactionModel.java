package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.api.rxjava.InternalApi;
import com.kryptokrauts.aeternity.generated.model.NamePointer;
import com.kryptokrauts.aeternity.generated.model.NameUpdateTx;
import com.kryptokrauts.aeternity.generated.model.Tx;
import com.kryptokrauts.aeternity.sdk.annotations.Mandatory;
import com.kryptokrauts.aeternity.sdk.constants.AENS;
import com.kryptokrauts.aeternity.sdk.constants.ApiIdentifiers;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.NameUpdateTransaction;
import com.kryptokrauts.aeternity.sdk.util.ValidationUtil;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
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
  @Default private BigInteger ttl = BigInteger.ZERO;

  private BigInteger nameTtl;
  private BigInteger clientTtl;

  @Default private Map<String, String> pointers = new HashMap<>();

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
    return pointers.entrySet().stream()
        .map(p -> new NamePointer().key(p.getKey()).id(p.getValue()))
        .collect(Collectors.toList());
  }

  @Override
  public Function<Tx, NameUpdateTransactionModel> getApiToModelFunction() {
    return (tx) ->
        this.toBuilder()
            .accountId(tx.getAccountId())
            .fee(tx.getFee())
            .nonce(tx.getNonce())
            .nameId(tx.getNameId())
            .nameTtl(tx.getNameTtl())
            .clientTtl(tx.getClientTtl())
            .ttl(tx.getTtl())
            .pointers(
                tx.getPointers().stream()
                    .collect(Collectors.toMap(p -> p.getKey(), p -> p.getId())))
            .build();
  }

  @Override
  public void validateInput() {
    // Validate parameters
    ValidationUtil.checkParameters(
        validate -> nameId != null,
        nameId,
        "validateNameUpdateTransaction",
        Arrays.asList("nameId"),
        ValidationUtil.PARAMETER_IS_NULL);
    ValidationUtil.checkParameters(
        validate -> nameId.startsWith(ApiIdentifiers.NAME),
        nameId,
        "validateNameUpdateTransaction",
        Arrays.asList("nameId"),
        ValidationUtil.MISSING_API_IDENTIFIER);
    ValidationUtil.checkParameters(
        validate -> checkDefaultPointerTypes(pointers),
        pointers,
        "validateNameUpdateTransaction",
        Arrays.asList("pointers"),
        ValidationUtil.INVALID_STANDARD_POINTER);
  }

  @Override
  public AbstractTransaction<?> buildTransaction(ExternalApi externalApi, InternalApi internalApi) {
    return NameUpdateTransaction.builder()
        .externalApi(externalApi)
        .internalApi(internalApi)
        .model(this)
        .build();
  }

  private Boolean checkDefaultPointerTypes(final Map<String, String> pointers) {
    for (Map.Entry<String, String> entry : AENS.POINTERKEY_TO_IDENTIFIER_MAP.entrySet()) {
      String value = pointers.get(entry.getKey());
      if (value != null && !value.matches("^" + entry.getValue() + "_.+$")) {
        return false;
      }
    }
    return true;
  }
}
