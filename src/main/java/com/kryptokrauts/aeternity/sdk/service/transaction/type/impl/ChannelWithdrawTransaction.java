package com.kryptokrauts.aeternity.sdk.service.transaction.type.impl;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.constants.ApiIdentifiers;
import com.kryptokrauts.aeternity.sdk.constants.SerializationTags;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.ChannelWithdrawTransactionModel;
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
public class ChannelWithdrawTransaction
    extends AbstractTransaction<ChannelWithdrawTransactionModel> {

  @NonNull private ExternalApi externalApi;

  @Override
  protected Single<UnsignedTx> createInternal() {
    return externalApi.rxPostChannelWithdraw(model.toApiModel());
  }

  @Override
  protected Bytes createRLPEncodedList() {
    Bytes encodedRlp =
        RLP.encodeList(
            rlpWriter -> {
              rlpWriter.writeInt(SerializationTags.OBJECT_TAG_CHANNEL_WITHDRAW_TRANSACTION);
              rlpWriter.writeInt(SerializationTags.VSN_1);
              byte[] channelIdWithTag =
                  EncodingUtils.decodeCheckAndTag(
                      model.getChannelId(), Arrays.asList(ApiIdentifiers.CHANNEL));
              byte[] toIdWithTag =
                  EncodingUtils.decodeCheckAndTag(
                      model.getToId(), Arrays.asList(ApiIdentifiers.ACCOUNT_PUBKEY));
              rlpWriter.writeByteArray(channelIdWithTag);
              rlpWriter.writeByteArray(toIdWithTag);
              rlpWriter.writeBigInteger(model.getAmount());
              rlpWriter.writeBigInteger(model.getTtl());
              rlpWriter.writeBigInteger(model.getFee());
              rlpWriter.writeString(model.getStateHash());
              rlpWriter.writeBigInteger(model.getRound());
              rlpWriter.writeBigInteger(model.getNonce());
            });
    return encodedRlp;
  }
}
