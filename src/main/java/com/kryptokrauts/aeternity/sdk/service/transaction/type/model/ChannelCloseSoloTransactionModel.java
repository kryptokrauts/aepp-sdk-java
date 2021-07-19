package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.api.rxjava.InternalApi;
import com.kryptokrauts.aeternity.generated.model.ChannelCloseSoloTx;
import com.kryptokrauts.aeternity.generated.model.Tx;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.ChannelCloseSoloTransaction;
import java.math.BigInteger;
import java.util.function.Function;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@ToString
public class ChannelCloseSoloTransactionModel extends AbstractTransactionModel<ChannelCloseSoloTx> {

  private String channelId;

  private String fromId;

  private String payload;

  private String poi;

  private BigInteger ttl;

  private BigInteger nonce;

  @Override
  public ChannelCloseSoloTx toApiModel() {
    ChannelCloseSoloTx channelCloseSoloTx = new ChannelCloseSoloTx();
    channelCloseSoloTx.setChannelId(channelId);
    channelCloseSoloTx.setFromId(fromId);
    channelCloseSoloTx.setPayload(payload);
    channelCloseSoloTx.setPoi(poi);
    channelCloseSoloTx.setFee(fee);
    channelCloseSoloTx.setTtl(ttl);
    channelCloseSoloTx.setNonce(nonce);
    return channelCloseSoloTx;
  }

  @Override
  public Function<Tx, ChannelCloseSoloTransactionModel> getApiToModelFunction() {
    return (tx) ->
        this.toBuilder()
            .channelId(tx.getChannelId())
            .fromId(tx.getFromId())
            .payload(tx.getPayload())
            .poi(tx.getPoi())
            .ttl(tx.getTtl())
            .fee(tx.getFee())
            .nonce(tx.getNonce())
            .build();
  }

  @Override
  public void validateInput() {}

  @Override
  public AbstractTransaction<?> buildTransaction(ExternalApi externalApi, InternalApi internalApi) {
    return ChannelCloseSoloTransaction.builder()
        .externalApi(externalApi)
        .internalApi(internalApi)
        .model(this)
        .build();
  }
}
