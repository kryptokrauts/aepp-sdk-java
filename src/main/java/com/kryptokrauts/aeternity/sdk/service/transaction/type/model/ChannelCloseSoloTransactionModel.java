package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.ChannelCloseSoloTx;
import com.kryptokrauts.aeternity.generated.model.GenericTx;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.ChannelCloseSoloTransaction;
import com.kryptokrauts.sophia.compiler.generated.api.rxjava.DefaultApi;
import java.math.BigInteger;
import java.util.function.Function;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
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
  public Function<GenericTx, ChannelCloseSoloTransactionModel> getApiToModelFunction() {
    return (tx) -> {
      ChannelCloseSoloTx castedTx = (ChannelCloseSoloTx) tx;
      return this.toBuilder()
          .channelId(castedTx.getChannelId())
          .fromId(castedTx.getFromId())
          .payload(castedTx.getPayload())
          .poi(castedTx.getPoi())
          .ttl(castedTx.getTtl())
          .fee(castedTx.getFee())
          .nonce(castedTx.getNonce())
          .build();
    };
  }

  @Override
  public void validateInput() {}

  @Override
  public AbstractTransaction<?> buildTransaction(ExternalApi externalApi, DefaultApi compilerApi) {
    return ChannelCloseSoloTransaction.builder().externalApi(externalApi).model(this).build();
  }
}
