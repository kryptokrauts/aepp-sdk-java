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
import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.rlp.RLP;

@Getter
@SuperBuilder
public class ChannelSnapshotSoloTransaction extends AbstractTransaction<ChannelSnapshotSoloTx> {

  @NonNull private String channelId;
  @NonNull private String fromId;
  @NonNull private String payload;
  @NonNull private BigInteger ttl;
  @NonNull private BigInteger nonce;
  @NonNull private ChannelApi channelApi;

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

  @Override
  protected void validateInput() {
    // nothing to validate here
  }

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
              this.checkZeroAndWriteValue(rlpWriter, this.ttl);
              this.checkZeroAndWriteValue(rlpWriter, this.fee);
              this.checkZeroAndWriteValue(rlpWriter, this.nonce);
            });
    return encodedRlp;
  }
}
