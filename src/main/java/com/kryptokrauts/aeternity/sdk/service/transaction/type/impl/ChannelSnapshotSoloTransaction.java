package com.kryptokrauts.aeternity.sdk.service.transaction.type.impl;

import com.kryptokrauts.aeternity.generated.api.rxjava.ChannelApi;
import com.kryptokrauts.aeternity.generated.model.ChannelSnapshotSoloTx;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import io.reactivex.Single;
import java.math.BigInteger;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import net.consensys.cava.bytes.Bytes;
import net.consensys.cava.rlp.RLP;

@Getter
@SuperBuilder
public class ChannelSnapshotSoloTransaction extends AbstractTransaction<ChannelSnapshotSoloTx> {

  @NonNull String channelId;
  @NonNull String fromId;
  @NonNull String payload;
  @NonNull BigInteger ttl;
  @NonNull BigInteger nonce;
  @NonNull private ChannelApi channelApi;

  @Override
  protected Bytes createRLPEncodedList() {
    // TODO
    Bytes encodedRlp = RLP.encodeList(rlpWriter -> {});
    throw new UnsupportedOperationException();
  }

  @Override
  protected Single<UnsignedTx> createInternal() {
    return channelApi.rxPostChannelSnapshotSolo(toModel());
  }

  @Override
  protected ChannelSnapshotSoloTx toModel() {
    ChannelSnapshotSoloTx channelSnapshotSoloTx = new ChannelSnapshotSoloTx();
    channelSnapshotSoloTx.setChannelId(channelId);
    channelSnapshotSoloTx.setFromId(fromId);
    channelSnapshotSoloTx.setPayload(payload);
    channelSnapshotSoloTx.setFee(fee);
    channelSnapshotSoloTx.setTtl(ttl);
    channelSnapshotSoloTx.setNonce(nonce);
    return channelSnapshotSoloTx;
  }
}
