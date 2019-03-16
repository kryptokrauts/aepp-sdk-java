package com.kryptokrauts.aeternity.sdk.service.transaction.type.impl;

import com.kryptokrauts.aeternity.generated.api.rxjava.ChannelApi;
import com.kryptokrauts.aeternity.generated.model.ChannelCloseSoloTx;
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
public class ChannelCloseSoloTransaction extends AbstractTransaction<ChannelCloseSoloTx> {

  @NonNull String channelId;
  @NonNull String fromId;
  @NonNull String payload;
  @NonNull String poi;
  @NonNull BigInteger ttl;
  @NonNull BigInteger nonce;
  @NonNull private ChannelApi channelApi;

  @Override
  protected Bytes createRLPEncodedList() {
    Bytes encodedRlp =
        RLP.encodeList(
            rlpWriter -> {
              rlpWriter.writeInt(SerializationTags.OBJECT_TAG_CHANNEL_CLOSE_SOLO_TRANSACTION);
              rlpWriter.writeInt(SerializationTags.VSN);
              byte[] channelIdWithTag =
                  EncodingUtils.decodeCheckAndTag(this.channelId, SerializationTags.ID_TAG_CHANNEL);
              byte[] fromIdWithTag =
                  EncodingUtils.decodeCheckAndTag(this.fromId, SerializationTags.ID_TAG_ACCOUNT);
              rlpWriter.writeByteArray(channelIdWithTag);
              rlpWriter.writeByteArray(fromIdWithTag);
              rlpWriter.writeString(this.payload);
              rlpWriter.writeString(
                  this.poi); // TODO inform about Proof of Inclusion and how it is handled
              rlpWriter.writeBigInteger(this.ttl);
              rlpWriter.writeBigInteger(this.fee);
              rlpWriter.writeBigInteger(this.nonce);
            });
    return encodedRlp;
  }

  @Override
  protected Single<UnsignedTx> createInternal() {
    return channelApi.rxPostChannelCloseSolo(toModel());
  }

  @Override
  protected ChannelCloseSoloTx toModel() {
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
}
