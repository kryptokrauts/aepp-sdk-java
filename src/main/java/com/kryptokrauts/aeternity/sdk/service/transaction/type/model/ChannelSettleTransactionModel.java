package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.ChannelSettleTx;
import com.kryptokrauts.aeternity.generated.model.GenericTx;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.ChannelSettleTransaction;
import com.kryptokrauts.sophia.compiler.generated.api.rxjava.DefaultApi;
import java.math.BigInteger;
import java.util.function.Function;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@ToString
public class ChannelSettleTransactionModel extends AbstractTransactionModel<ChannelSettleTx> {

  private String channelId;

  private String fromId;

  private BigInteger initiatorAmountFinal;

  private BigInteger responderAmountFinal;

  private BigInteger ttl;

  private BigInteger nonce;

  @Override
  public ChannelSettleTx toApiModel() {
    ChannelSettleTx channelSettleTx = new ChannelSettleTx();
    channelSettleTx.setChannelId(channelId);
    channelSettleTx.setFromId(fromId);
    channelSettleTx.setInitiatorAmountFinal(initiatorAmountFinal);
    channelSettleTx.setResponderAmountFinal(responderAmountFinal);
    channelSettleTx.setFee(fee);
    channelSettleTx.setTtl(ttl);
    channelSettleTx.setNonce(nonce);
    return channelSettleTx;
  }

  @Override
  public Function<GenericTx, ChannelSettleTransactionModel> getApiToModelFunction() {
    return (tx) -> {
      ChannelSettleTx castedTx = (ChannelSettleTx) tx;
      return this.toBuilder()
          .channelId(castedTx.getChannelId())
          .fromId(castedTx.getFromId())
          .initiatorAmountFinal(castedTx.getInitiatorAmountFinal())
          .responderAmountFinal(castedTx.getResponderAmountFinal())
          .fee(castedTx.getFee())
          .ttl(castedTx.getTtl())
          .nonce(castedTx.getNonce())
          .build();
    };
  }

  @Override
  public void validateInput() {}

  @Override
  public AbstractTransaction<?> buildTransaction(ExternalApi externalApi, DefaultApi compilerApi) {
    return ChannelSettleTransaction.builder().externalApi(externalApi).model(this).build();
  }
}
