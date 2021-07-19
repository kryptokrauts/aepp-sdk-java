package com.kryptokrauts.aeternity.sdk.service.transaction.type.impl;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.api.rxjava.InternalApi;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.constants.ApiIdentifiers;
import com.kryptokrauts.aeternity.sdk.constants.SerializationTags;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.ChannelSettleTransactionModel;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import io.reactivex.Single;
import java.util.Arrays;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.rlp.RLP;

@SuperBuilder
@ToString
public class ChannelSettleTransaction extends AbstractTransaction<ChannelSettleTransactionModel> {

  @NonNull private ExternalApi externalApi;
  @NonNull private InternalApi internalApi;

  @Override
  protected Single<UnsignedTx> createInternal() {
    return internalApi.rxPostChannelSettle(model.toApiModel(), false, null);
  }

  @Override
  public Bytes createRLPEncodedList() {
    Bytes encodedRlp =
        RLP.encodeList(
            rlpWriter -> {
              rlpWriter.writeInt(SerializationTags.OBJECT_TAG_CHANNEL_SETTLE_TRANSACTION);
              rlpWriter.writeInt(SerializationTags.VSN_1);
              byte[] channelIdWithTag =
                  EncodingUtils.decodeCheckAndTag(
                      model.getChannelId(), Arrays.asList(ApiIdentifiers.CHANNEL));
              byte[] fromIdWithTag =
                  EncodingUtils.decodeCheckAndTag(
                      model.getFromId(), Arrays.asList(ApiIdentifiers.ACCOUNT_PUBKEY));
              rlpWriter.writeByteArray(channelIdWithTag);
              rlpWriter.writeByteArray(fromIdWithTag);
              this.checkZeroAndWriteValue(rlpWriter, model.getInitiatorAmountFinal());
              this.checkZeroAndWriteValue(rlpWriter, model.getResponderAmountFinal());
              this.checkZeroAndWriteValue(rlpWriter, model.getTtl());
              this.checkZeroAndWriteValue(rlpWriter, model.getFee());
              this.checkZeroAndWriteValue(rlpWriter, model.getNonce());
            });
    return encodedRlp;
  }
}
