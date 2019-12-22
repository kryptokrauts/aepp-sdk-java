package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.GenericTx;
import com.kryptokrauts.aeternity.generated.model.SpendTx;
import com.kryptokrauts.aeternity.sdk.annotations.Mandatory;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.SpendTransaction;
import com.kryptokrauts.sophia.compiler.generated.api.rxjava.DefaultApi;
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
  @Mandatory private BigInteger ttl;
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
  public Function<GenericTx, SpendTransactionModel> getApiToModelFunction() {
    return (tx) -> {
      SpendTx castedTx = (SpendTx) tx;
      return this.toBuilder()
          .sender(castedTx.getSenderId())
          .recipient(castedTx.getRecipientId())
          .amount(castedTx.getAmount())
          .payload(castedTx.getPayload())
          .fee(castedTx.getFee())
          .nonce(castedTx.getNonce())
          .ttl(castedTx.getTtl())
          .build();
    };
  }

  @Override
  public void validateInput() {
    // nothing to validate here
  }

  @Override
  public SpendTransaction buildTransaction(ExternalApi externalApi, DefaultApi compilerApi) {
    return SpendTransaction.builder().externalApi(externalApi).model(this).build();
  }
}
