package com.kryptokrauts.aeternity.sdk.service.transaction.type.impl;

import com.kryptokrauts.aeternity.generated.api.rxjava.ChannelApi;
import com.kryptokrauts.aeternity.generated.model.ChannelSettleTx;
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
public class ChannelSettleTransaction extends AbstractTransaction<ChannelSettleTx> {

  @NonNull String channelId;
  @NonNull String fromId;
  @NonNull BigInteger initiatorAmountFinal;
  @NonNull BigInteger responderAmountFinal;
  @NonNull BigInteger ttl;
  @NonNull BigInteger nonce;
  @NonNull private ChannelApi channelApi;

  @Override
  protected Bytes createRLPEncodedList() {
    Bytes encodedRlp =
        RLP.encodeList(
            rlpWriter -> {
              rlpWriter.writeInt(SerializationTags.OBJECT_TAG_CHANNEL_SETTLE_TRANSACTION);
              rlpWriter.writeInt(SerializationTags.VSN);
              byte[] channelIdWithTag =
                  EncodingUtils.decodeCheckAndTag(this.channelId, SerializationTags.ID_TAG_CHANNEL);
              byte[] fromIdWithTag =
                  EncodingUtils.decodeCheckAndTag(this.fromId, SerializationTags.ID_TAG_ACCOUNT);
              rlpWriter.writeByteArray(channelIdWithTag);
              rlpWriter.writeByteArray(fromIdWithTag);
              rlpWriter.writeBigInteger(this.initiatorAmountFinal);
              rlpWriter.writeBigInteger(this.responderAmountFinal);
              rlpWriter.writeBigInteger(this.ttl);
              rlpWriter.writeBigInteger(this.fee);
              rlpWriter.writeBigInteger(this.nonce);
            });
    return encodedRlp;
  }

  @Override
  protected Single<UnsignedTx> createInternal() {
    return channelApi.rxPostChannelSettle(toModel());
  }

  @Override
  protected ChannelSettleTx toModel() {
    ChannelSettleTx channelSettleTx = new ChannelSettleTx();
    channelSettleTx.setChannelId(channelId);
    channelSettleTx.setFromId(fromId);
    channelSettleTx.setInitiatorAmountFinal(initiatorAmountFinal);
    channelSettleTx.setResponderAmountFinal(responderAmountFinal);
    channelSettleTx.setFee(fee);
    channelSettleTx.setTtl(ttl);
    channelSettleTx.setNonce(nonce);
    return channelSettleTx;
  }
}
