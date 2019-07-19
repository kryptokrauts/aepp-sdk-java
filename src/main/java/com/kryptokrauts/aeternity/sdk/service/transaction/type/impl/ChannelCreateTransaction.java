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
import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.rlp.RLP;

@Getter
@SuperBuilder
public class ChannelCreateTransaction extends AbstractTransaction<ChannelCreateTx> {

  @NonNull private String initiator;
  @NonNull private BigInteger initiatorAmount;
  @NonNull private String responder;
  @NonNull private BigInteger responderAmount;
  @NonNull private BigInteger channelReserve;
  @NonNull private BigInteger lockPeriod;
  @NonNull private BigInteger ttl;
  @NonNull private String stateHash;
  @NonNull private BigInteger nonce;
  @NonNull private ChannelApi channelApi;

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

  @Override
  protected void validateInput() {
    // nothing to validate here
  }

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
              this.checkZeroAndWriteValue(rlpWriter, this.initiatorAmount);
              byte[] responderIdWithTag =
                  EncodingUtils.decodeCheckAndTag(this.responder, SerializationTags.ID_TAG_ACCOUNT);
              rlpWriter.writeByteArray(responderIdWithTag);
              this.checkZeroAndWriteValue(rlpWriter, this.responderAmount);
              this.checkZeroAndWriteValue(rlpWriter, this.channelReserve);
              this.checkZeroAndWriteValue(rlpWriter, this.lockPeriod);
              this.checkZeroAndWriteValue(rlpWriter, this.ttl);
              this.checkZeroAndWriteValue(rlpWriter, this.fee);
              rlpWriter.writeString(this.stateHash);
              this.checkZeroAndWriteValue(rlpWriter, this.nonce);
            });
    return encodedRlp;
  }
}
