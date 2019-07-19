package com.kryptokrauts.aeternity.sdk.service.transaction.type.impl;

import com.kryptokrauts.aeternity.generated.api.rxjava.ChannelApi;
import com.kryptokrauts.aeternity.generated.model.ChannelWithdrawTx;
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
public class ChannelWithdrawTransaction extends AbstractTransaction<ChannelWithdrawTx> {

  @NonNull private String channelId;
  @NonNull private String toId;
  @NonNull private BigInteger amount;
  @NonNull private BigInteger ttl;
  @NonNull private String stateHash;
  @NonNull private BigInteger round;
  @NonNull private BigInteger nonce;
  @NonNull private ChannelApi channelApi;

  @Override
  protected Single<UnsignedTx> createInternal() {
    return channelApi.rxPostChannelWithdraw(toModel());
  }

  @Override
  protected ChannelWithdrawTx toModel() {
    ChannelWithdrawTx channelWithdrawTx = new ChannelWithdrawTx();
    channelWithdrawTx.setChannelId(channelId);
    channelWithdrawTx.setToId(toId);
    channelWithdrawTx.setAmount(amount);
    channelWithdrawTx.setFee(fee);
    channelWithdrawTx.setTtl(ttl);
    channelWithdrawTx.setStateHash(stateHash);
    channelWithdrawTx.setRound(round);
    channelWithdrawTx.setNonce(nonce);
    return channelWithdrawTx;
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
              rlpWriter.writeInt(SerializationTags.OBJECT_TAG_CHANNEL_WITHDRAW_TRANSACTION);
              rlpWriter.writeInt(SerializationTags.VSN);
              byte[] channelIdWithTag =
                  EncodingUtils.decodeCheckAndTag(this.channelId, SerializationTags.ID_TAG_CHANNEL);
              byte[] toIdWithTag =
                  EncodingUtils.decodeCheckAndTag(this.toId, SerializationTags.ID_TAG_ACCOUNT);
              rlpWriter.writeByteArray(channelIdWithTag);
              rlpWriter.writeByteArray(toIdWithTag);
              rlpWriter.writeBigInteger(this.amount);
              rlpWriter.writeBigInteger(this.ttl);
              rlpWriter.writeBigInteger(this.fee);
              rlpWriter.writeString(this.stateHash);
              rlpWriter.writeBigInteger(this.round);
              rlpWriter.writeBigInteger(this.nonce);
            });
    return encodedRlp;
  }
}
