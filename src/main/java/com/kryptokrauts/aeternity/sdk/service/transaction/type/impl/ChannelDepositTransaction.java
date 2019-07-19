package com.kryptokrauts.aeternity.sdk.service.transaction.type.impl;

import com.kryptokrauts.aeternity.generated.api.rxjava.ChannelApi;
import com.kryptokrauts.aeternity.generated.model.ChannelDepositTx;
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
public class ChannelDepositTransaction extends AbstractTransaction<ChannelDepositTx> {

  @NonNull private String channelId;
  @NonNull private String fromId;
  @NonNull private BigInteger amount;
  @NonNull private BigInteger ttl;
  @NonNull private String stateHash;
  @NonNull private BigInteger round;
  @NonNull private BigInteger nonce;
  @NonNull private ChannelApi channelApi;

  @Override
  protected Single<UnsignedTx> createInternal() {
    return channelApi.rxPostChannelDeposit(toModel());
  }

  @Override
  protected ChannelDepositTx toModel() {
    ChannelDepositTx channelDepositTx = new ChannelDepositTx();
    channelDepositTx.setChannelId(channelId);
    channelDepositTx.setFromId(fromId);
    channelDepositTx.setAmount(amount);
    channelDepositTx.fee(fee);
    channelDepositTx.setTtl(ttl);
    channelDepositTx.setStateHash(stateHash);
    channelDepositTx.setRound(round);
    channelDepositTx.setNonce(nonce);

    return channelDepositTx;
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
              rlpWriter.writeInt(SerializationTags.OBJECT_TAG_CHANNEL_DEPOSIT_TRANSACTION);
              rlpWriter.writeInt(SerializationTags.VSN);
              byte[] channelIdWithTag =
                  EncodingUtils.decodeCheckAndTag(this.channelId, SerializationTags.ID_TAG_CHANNEL);
              byte[] fromIdWithTag =
                  EncodingUtils.decodeCheckAndTag(this.fromId, SerializationTags.ID_TAG_ACCOUNT);
              rlpWriter.writeByteArray(channelIdWithTag);
              rlpWriter.writeByteArray(fromIdWithTag);
              this.checkZeroAndWriteValue(rlpWriter, this.amount);
              this.checkZeroAndWriteValue(rlpWriter, this.ttl);
              this.checkZeroAndWriteValue(rlpWriter, this.fee);
              rlpWriter.writeString(this.stateHash);
              this.checkZeroAndWriteValue(rlpWriter, this.round);
              this.checkZeroAndWriteValue(rlpWriter, this.nonce);
            });
    return encodedRlp;
  }
}
