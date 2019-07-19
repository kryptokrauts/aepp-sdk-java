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
import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.rlp.RLP;

@Getter
@SuperBuilder
public class ChannelSettleTransaction extends AbstractTransaction<ChannelSettleTx> {

  @NonNull private String channelId;
  @NonNull private String fromId;
  @NonNull private BigInteger initiatorAmountFinal;
  @NonNull private BigInteger responderAmountFinal;
  @NonNull private BigInteger ttl;
  @NonNull private BigInteger nonce;
  @NonNull private ChannelApi channelApi;

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

  @Override
  protected void validateInput() {
    // nothing to validate here
  }

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
              this.checkZeroAndWriteValue(rlpWriter, this.initiatorAmountFinal);
              this.checkZeroAndWriteValue(rlpWriter, this.responderAmountFinal);
              this.checkZeroAndWriteValue(rlpWriter, this.ttl);
              this.checkZeroAndWriteValue(rlpWriter, this.fee);
              this.checkZeroAndWriteValue(rlpWriter, this.nonce);
            });
    return encodedRlp;
  }
}
