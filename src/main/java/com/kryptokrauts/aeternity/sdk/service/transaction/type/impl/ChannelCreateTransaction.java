package com.kryptokrauts.aeternity.sdk.service.transaction.type.impl;

import com.kryptokrauts.aeternity.generated.api.rxjava.ChannelApi;
import com.kryptokrauts.aeternity.generated.model.ChannelCreateTx;
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
public class ChannelCreateTransaction extends AbstractTransaction<ChannelCreateTx> {

  @NonNull String initiator;
  @NonNull BigInteger initiatorAmount;
  @NonNull String responder;
  @NonNull BigInteger responderAmount;
  @NonNull BigInteger channelReserve;
  @NonNull BigInteger lockPeriod;
  @NonNull BigInteger ttl;
  @NonNull String stateHash;
  @NonNull BigInteger nonce;
  @NonNull private ChannelApi channelApi;

  @Override
  protected Bytes createRLPEncodedList() {
    Bytes encodedRlp =
        RLP.encodeList(
            rlpWriter -> {
              rlpWriter.writeInt(SerializationTags.OBJECT_TAG_CHANNEL_CREATE_TRANSACTION);
              rlpWriter.writeInt(SerializationTags.VSN);
              byte[] initiatorIdWithTag =
                  EncodingUtils.decodeCheckAndTag(this.initiator, SerializationTags.ID_TAG_ACCOUNT);
              rlpWriter.writeByteArray(initiatorIdWithTag);
              rlpWriter.writeBigInteger(this.initiatorAmount);
              byte[] responderIdWithTag =
                  EncodingUtils.decodeCheckAndTag(this.responder, SerializationTags.ID_TAG_ACCOUNT);
              rlpWriter.writeByteArray(responderIdWithTag);
              rlpWriter.writeBigInteger(this.responderAmount);
              rlpWriter.writeBigInteger(this.channelReserve);
              rlpWriter.writeBigInteger(this.lockPeriod);
              rlpWriter.writeBigInteger(this.ttl);
              rlpWriter.writeBigInteger(this.fee);
              rlpWriter.writeString(this.stateHash);
              rlpWriter.writeBigInteger(this.nonce);
            });
    return encodedRlp;
  }

  @Override
  protected Single<UnsignedTx> createInternal() {
    return channelApi.rxPostChannelCreate(toModel());
  }

  @Override
  protected ChannelCreateTx toModel() {
    ChannelCreateTx channelCreateTx = new ChannelCreateTx();
    channelCreateTx.setInitiatorId(initiator);
    channelCreateTx.setInitiatorAmount(initiatorAmount);
    channelCreateTx.setResponderId(responder);
    channelCreateTx.setResponderAmount(responderAmount);
    channelCreateTx.setChannelReserve(channelReserve);
    channelCreateTx.setLockPeriod(lockPeriod);
    channelCreateTx.setFee(fee);
    channelCreateTx.setTtl(ttl);
    channelCreateTx.setStateHash(stateHash);
    channelCreateTx.setNonce(nonce);
    return channelCreateTx;
  }
}
