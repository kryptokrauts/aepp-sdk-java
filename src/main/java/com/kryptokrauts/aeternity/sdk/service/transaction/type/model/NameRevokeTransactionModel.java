package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.api.rxjava.InternalApi;
import com.kryptokrauts.aeternity.generated.model.NameRevokeTx;
import com.kryptokrauts.aeternity.generated.model.Tx;
import com.kryptokrauts.aeternity.sdk.annotations.Mandatory;
import com.kryptokrauts.aeternity.sdk.constants.ApiIdentifiers;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.NameRevokeTransaction;
import com.kryptokrauts.aeternity.sdk.util.ValidationUtil;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.function.Function;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@ToString
public class NameRevokeTransactionModel extends AbstractTransactionModel<NameRevokeTx> {

  @Mandatory private String accountId;
  @Mandatory private BigInteger nonce;
  @Mandatory private String nameId;
  @Default private BigInteger ttl = BigInteger.ZERO;

  @Override
  public NameRevokeTx toApiModel() {
    NameRevokeTx nameRevokeTx = new NameRevokeTx();
    nameRevokeTx.setAccountId(this.accountId);
    nameRevokeTx.setNonce(this.nonce);
    nameRevokeTx.setNameId(nameId);
    nameRevokeTx.setFee(this.fee);
    nameRevokeTx.setTtl(this.ttl);
    return nameRevokeTx;
  }

  @Override
  public Function<Tx, NameRevokeTransactionModel> getApiToModelFunction() {
    return (tx) ->
        this.toBuilder()
            .accountId(tx.getAccountId())
            .nonce(tx.getNonce())
            .nameId(tx.getNameId())
            .ttl(tx.getTtl())
            .build();
  }

  @Override
  public void validateInput() {
    // Validate parameters
    ValidationUtil.checkParameters(
        validate -> nameId.startsWith(ApiIdentifiers.NAME),
        nameId,
        "validateNameRevokeTransaction",
        Arrays.asList("nameId", ApiIdentifiers.NAME),
        ValidationUtil.MISSING_API_IDENTIFIER);
  }

  @Override
  public AbstractTransaction<?> buildTransaction(ExternalApi externalApi, InternalApi internalApi) {
    return NameRevokeTransaction.builder()
        .externalApi(externalApi)
        .internalApi(internalApi)
        .model(this)
        .build();
  }
}
