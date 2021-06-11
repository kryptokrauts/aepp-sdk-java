package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.GenericTx;
import com.kryptokrauts.aeternity.generated.model.PayingForTx;
import com.kryptokrauts.aeternity.sdk.annotations.Mandatory;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.PayingForTransaction;
import com.kryptokrauts.sophia.compiler.generated.api.rxjava.DefaultApi;
import java.math.BigInteger;
import java.util.function.Function;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@ToString
public class PayingForTransactionModel extends AbstractTransactionModel<PayingForTx> {

  @Mandatory private String payerId;
  @Mandatory private BigInteger nonce;
  @Mandatory private String innerTx;

  @Override
  public PayingForTx toApiModel() {
    PayingForTx payingForTx = new PayingForTx();
    payingForTx.payerId(payerId);
    payingForTx.nonce(nonce);
    payingForTx.fee(fee);
    /**
     * we cannot map the inner tx because we need a GenericSignedTx here which cannot be produced
     * from the model class without using the {@link TransactionServiceImpl} which is not allowed
     * here
     */
    return payingForTx;
  }

  @Override
  public void validateInput() {}

  @Override
  public AbstractTransaction<?> buildTransaction(ExternalApi externalApi, DefaultApi compilerApi) {
    return PayingForTransaction.builder().externalApi(externalApi).model(this).build();
  }

  @Override
  public Function<GenericTx, ?> getApiToModelFunction() {
    return (tx) -> {
      PayingForTx castedTx = (PayingForTx) tx;
      return this.toBuilder()
          .payerId(castedTx.getPayerId())
          .fee(castedTx.getFee())
          .nonce(castedTx.getNonce())
          .build();
    };
  }
}
