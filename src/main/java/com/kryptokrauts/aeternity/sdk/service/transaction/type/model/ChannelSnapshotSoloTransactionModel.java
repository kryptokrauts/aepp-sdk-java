package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.ChannelSnapshotSoloTx;
import com.kryptokrauts.aeternity.generated.model.GenericTx;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.ChannelSnapshotSoloTransaction;
import com.kryptokrauts.sophia.compiler.generated.api.rxjava.DefaultApi;
import java.math.BigInteger;
import java.util.function.Function;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@ToString
public class ChannelSnapshotSoloTransactionModel
    extends AbstractTransactionModel<ChannelSnapshotSoloTx> {

  private String channelId;
  private String fromId;
  private String payload;
  private BigInteger ttl;
  private BigInteger nonce;

  @Override
  public ChannelSnapshotSoloTx toApiModel() {
    ChannelSnapshotSoloTx channelSnapshotSoloTx = new ChannelSnapshotSoloTx();
    channelSnapshotSoloTx.setChannelId(channelId);
    channelSnapshotSoloTx.setFromId(fromId);
    channelSnapshotSoloTx.setPayload(payload);
    channelSnapshotSoloTx.setFee(fee);
    channelSnapshotSoloTx.setTtl(ttl);
    channelSnapshotSoloTx.setNonce(nonce);
    return channelSnapshotSoloTx;
  }

  @Override
  public Function<GenericTx, ChannelSnapshotSoloTransactionModel> getApiToModelFunction() {
    return (tx) -> {
      ChannelSnapshotSoloTx castedTx = (ChannelSnapshotSoloTx) tx;
      return this.toBuilder()
          .channelId(castedTx.getChannelId())
          .fromId(castedTx.getFromId())
          .fee(castedTx.getFee())
          .payload(castedTx.getPayload())
          .ttl(castedTx.getTtl())
          .nonce(castedTx.getNonce())
          .build();
    };
  }

  @Override
  public void validateInput() {}

  @Override
  public AbstractTransaction<?> buildTransaction(ExternalApi externalApi, DefaultApi compilerApi) {
    return ChannelSnapshotSoloTransaction.builder().externalApi(externalApi).model(this).build();
  }
}
