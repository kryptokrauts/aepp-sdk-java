package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.ChannelSlashTx;
import com.kryptokrauts.aeternity.generated.model.GenericTx;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.ChannelSlashTransaction;
import com.kryptokrauts.sophia.compiler.generated.api.rxjava.DefaultApi;
import java.math.BigInteger;
import java.util.function.Function;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
public class ChannelSlashTransactionModel extends AbstractTransactionModel<ChannelSlashTx> {

  private String channelId;
  private String fromId;
  private String payload;
  private String poi;
  private BigInteger ttl;
  private BigInteger nonce;

  @Override
  public ChannelSlashTx toApiModel() {
    ChannelSlashTx channelSlashTx = new ChannelSlashTx();
    channelSlashTx.setChannelId(channelId);
    channelSlashTx.setFromId(fromId);
    channelSlashTx.setPayload(payload);
    channelSlashTx.setPoi(poi);
    channelSlashTx.setFee(fee);
    channelSlashTx.setTtl(ttl);
    channelSlashTx.setNonce(nonce);
    return channelSlashTx;
  }

  @Override
  public Function<GenericTx, ChannelSlashTransactionModel> getApiToModelFunction() {
    return (tx) -> {
      ChannelSlashTx castedTx = (ChannelSlashTx) tx;
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
    return ChannelSlashTransaction.builder().externalApi(externalApi).model(this).build();
  }
}
