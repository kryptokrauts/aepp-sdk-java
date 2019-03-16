package com.kryptokrauts.aeternity.sdk.service.transaction.type.impl;

import com.kryptokrauts.aeternity.generated.api.rxjava.ChannelApi;
import com.kryptokrauts.aeternity.generated.model.ChannelSnapshotSoloTx;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.constants.SerializationTags;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
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
    Bytes encodedRlp =
        RLP.encodeList(
            rlpWriter -> {
              rlpWriter.writeInt(SerializationTags.OBJECT_TAG_CHANNEL_SNAPSHOT_TRANSACTION);
              rlpWriter.writeInt(SerializationTags.VSN);
              byte[] channelIdWithTag =
                  EncodingUtils.decodeCheckAndTag(this.channelId, SerializationTags.ID_TAG_CHANNEL);
              byte[] fromIdWithTag =
                  EncodingUtils.decodeCheckAndTag(this.fromId, SerializationTags.ID_TAG_ACCOUNT);
              rlpWriter.writeByteArray(channelIdWithTag);
              rlpWriter.writeByteArray(fromIdWithTag);
              rlpWriter.writeString(this.payload);
              rlpWriter.writeBigInteger(this.ttl);
              rlpWriter.writeBigInteger(this.fee);
              rlpWriter.writeBigInteger(this.nonce);
            });
    return encodedRlp;
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
