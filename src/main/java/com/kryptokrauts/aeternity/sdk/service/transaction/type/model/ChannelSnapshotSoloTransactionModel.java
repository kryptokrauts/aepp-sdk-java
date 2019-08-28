package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.ChannelSnapshotSoloTx;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.ChannelSnapshotSoloTransaction;
import com.kryptokrauts.sophia.compiler.generated.api.rxjava.DefaultApi;
import java.math.BigInteger;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ChannelSnapshotSoloTransactionModel
    extends AbstractTransactionModel<ChannelSnapshotSoloTx> {

  @NonNull private String channelId;
  @NonNull private String fromId;
  @NonNull private String payload;
  @NonNull private BigInteger ttl;
  @NonNull private BigInteger nonce;

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
  public void validateInput() {}

  @Override
  public AbstractTransaction<?> buildTransaction(ExternalApi externalApi, DefaultApi compilerApi) {
    return ChannelSnapshotSoloTransaction.builder().externalApi(externalApi).model(this).build();
  }
}
