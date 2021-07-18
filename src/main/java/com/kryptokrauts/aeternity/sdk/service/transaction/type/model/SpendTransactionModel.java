package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.api.rxjava.InternalApi;
import com.kryptokrauts.aeternity.generated.model.SpendTx;
import com.kryptokrauts.aeternity.generated.model.Tx;
import com.kryptokrauts.aeternity.sdk.annotations.Mandatory;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.SpendTransaction;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import java.math.BigInteger;
import java.util.function.Function;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@ToString
public class SpendTransactionModel extends AbstractTransactionModel<SpendTx> {
  @Mandatory private String sender;
  @Mandatory private String recipient;
  @Mandatory private BigInteger amount;
  @Default private String payload = "";
  private String payloadDecoded;
  @Default private BigInteger ttl = BigInteger.ZERO;
  @Mandatory private BigInteger nonce;

  @Override
  public SpendTx toApiModel() {
    SpendTx spendTx = new SpendTx();
    spendTx.setSenderId(this.sender);
    spendTx.setRecipientId(this.recipient);
    spendTx.setAmount(this.amount);
    spendTx.setPayload(this.payload);
    spendTx.setFee(this.fee);
    spendTx.setTtl(this.ttl);
    spendTx.setNonce(this.nonce);

    return spendTx;
  }

  @Override
  public Function<Tx, SpendTransactionModel> getApiToModelFunction() {
    return (tx) ->
        this.toBuilder()
            .sender(tx.getSenderId())
            .recipient(tx.getRecipientId())
            .amount(tx.getAmount())
            .payload(tx.getPayload())
            .payloadDecoded(new String(EncodingUtils.decodeCheckWithIdentifier(tx.getPayload())))
            .fee(tx.getFee())
            .nonce(tx.getNonce())
            .ttl(tx.getTtl())
            .build();
  }

  @Override
  public void validateInput() {
    // nothing to validate here
  }

  @Override
  public SpendTransaction buildTransaction(ExternalApi externalApi, InternalApi internalApi) {
    return SpendTransaction.builder()
        .externalApi(externalApi)
        .internalApi(internalApi)
        .model(this)
        .build();
  }
}
