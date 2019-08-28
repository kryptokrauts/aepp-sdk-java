package com.kryptokrauts.aeternity.sdk.service.transaction.type.impl;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.constants.SerializationTags;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.ChannelCloseSoloTransactionModel;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import io.reactivex.Single;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.rlp.RLP;

@SuperBuilder
@ToString
public class ChannelCloseSoloTransaction
    extends AbstractTransaction<ChannelCloseSoloTransactionModel> {
  @NonNull private ExternalApi externalApi;

  @Override
  protected Single<UnsignedTx> createInternal() {
    return externalApi.rxPostChannelCloseSolo(model.toApiModel());
  }

  @Override
  protected Bytes createRLPEncodedList() {
    Bytes encodedRlp =
        RLP.encodeList(
            rlpWriter -> {
              rlpWriter.writeInt(SerializationTags.OBJECT_TAG_CHANNEL_CLOSE_SOLO_TRANSACTION);
              rlpWriter.writeInt(SerializationTags.VSN);
              byte[] channelIdWithTag =
                  EncodingUtils.decodeCheckAndTag(
                      model.getChannelId(), SerializationTags.ID_TAG_CHANNEL);
              byte[] fromIdWithTag =
                  EncodingUtils.decodeCheckAndTag(
                      model.getFromId(), SerializationTags.ID_TAG_ACCOUNT);
              rlpWriter.writeByteArray(channelIdWithTag);
              rlpWriter.writeByteArray(fromIdWithTag);
              rlpWriter.writeString(model.getPayload());
              rlpWriter.writeString(
                  model.getPoi()); // TODO inform about Proof of Inclusion and how it is handled
              this.checkZeroAndWriteValue(rlpWriter, model.getTtl());
              this.checkZeroAndWriteValue(rlpWriter, model.getFee());
              this.checkZeroAndWriteValue(rlpWriter, model.getNonce());
            });
    return encodedRlp;
  }
}
