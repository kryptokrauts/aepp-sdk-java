package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.api.rxjava.InternalApi;
import com.kryptokrauts.aeternity.generated.model.ChannelSlashTx;
import com.kryptokrauts.aeternity.generated.model.Tx;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.ChannelSlashTransaction;
import java.math.BigInteger;
import java.util.function.Function;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@ToString
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
  public Function<Tx, ChannelSlashTransactionModel> getApiToModelFunction() {
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
    return ChannelSlashTransaction.builder()
        .externalApi(externalApi)
        .internalApi(internalApi)
        .model(this)
        .build();
  }
}
