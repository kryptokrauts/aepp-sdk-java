package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.api.rxjava.InternalApi;
import com.kryptokrauts.aeternity.generated.model.NameTransferTx;
import com.kryptokrauts.aeternity.generated.model.Tx;
import com.kryptokrauts.aeternity.sdk.annotations.Mandatory;
import com.kryptokrauts.aeternity.sdk.constants.ApiIdentifiers;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.NameTransferTransaction;
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
public class NameTransferTransactionModel extends AbstractTransactionModel<NameTransferTx> {

  @Mandatory private String accountId;
  @Mandatory private BigInteger nonce;
  @Mandatory private String nameId;
  @Mandatory private String recipientId;
  @Default private BigInteger ttl = BigInteger.ZERO;

  @Override
  public NameTransferTx toApiModel() {
    NameTransferTx nameTransferTx = new NameTransferTx();
    nameTransferTx.setAccountId(this.accountId);
    nameTransferTx.setNonce(this.nonce);
    nameTransferTx.setNameId(nameId);
    nameTransferTx.setRecipientId(this.recipientId);
    nameTransferTx.setFee(this.fee);
    nameTransferTx.setTtl(this.ttl);
    return nameTransferTx;
  }

  @Override
  public Function<Tx, NameTransferTransactionModel> getApiToModelFunction() {
    return (tx) ->
        this.toBuilder()
            .accountId(tx.getAccountId())
            .nonce(tx.getNonce())
            .nameId(tx.getNameId())
            .recipientId(tx.getRecipientId())
            .ttl(tx.getTtl())
            .build();
  }

  @Override
  public void validateInput() {
    // Validate parameters
    ValidationUtil.checkParameters(
        validate -> nameId.startsWith(ApiIdentifiers.NAME),
        nameId,
        "validateNameTransferTransaction",
        Arrays.asList("nameId", ApiIdentifiers.NAME),
        ValidationUtil.MISSING_API_IDENTIFIER);
  }

  @Override
  public AbstractTransaction<?> buildTransaction(ExternalApi externalApi, InternalApi internalApi) {
    return NameTransferTransaction.builder()
        .externalApi(externalApi)
        .internalApi(internalApi)
        .model(this)
        .build();
  }
}
