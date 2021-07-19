package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.api.rxjava.InternalApi;
import com.kryptokrauts.aeternity.generated.model.ChannelSettleTx;
import com.kryptokrauts.aeternity.generated.model.Tx;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.ChannelSettleTransaction;
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
  public Function<Tx, ChannelSettleTransactionModel> getApiToModelFunction() {
    return (tx) ->
        this.toBuilder()
            .channelId(tx.getChannelId())
            .fromId(tx.getFromId())
            .initiatorAmountFinal(tx.getInitiatorAmountFinal())
            .responderAmountFinal(tx.getResponderAmountFinal())
            .fee(tx.getFee())
            .ttl(tx.getTtl())
            .nonce(tx.getNonce())
            .build();
  }

  @Override
  public void validateInput() {}

  @Override
  public AbstractTransaction<?> buildTransaction(ExternalApi externalApi, InternalApi internalApi) {
    return ChannelSettleTransaction.builder()
        .externalApi(externalApi)
        .internalApi(internalApi)
        .model(this)
        .build();
  }
}
