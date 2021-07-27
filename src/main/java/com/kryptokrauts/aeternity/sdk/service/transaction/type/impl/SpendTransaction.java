package com.kryptokrauts.aeternity.sdk.service.transaction.type.impl;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.api.rxjava.InternalApi;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.constants.ApiIdentifiers;
import com.kryptokrauts.aeternity.sdk.constants.SerializationTags;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.SpendTransactionModel;
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
public class SpendTransaction extends AbstractTransaction<SpendTransactionModel> {

  @NonNull private ExternalApi externalApi;
  @NonNull private InternalApi internalApi;

  @Override
  protected Single<UnsignedTx> createInternal() {
    return internalApi.rxPostSpend(model.toApiModel(), false);
  }

  @Override
  public Bytes createRLPEncodedList() {
    Bytes encodedRlp =
        RLP.encodeList(
            rlpWriter -> {
              rlpWriter.writeInt(SerializationTags.OBJECT_TAG_SPEND_TRANSACTION);
              rlpWriter.writeInt(SerializationTags.VSN_1);
              byte[] senderWithTag =
                  EncodingUtils.decodeCheckAndTag(
                      model.getSender(), Arrays.asList(ApiIdentifiers.ACCOUNT_PUBKEY));
              byte[] recipientWithTag =
                  EncodingUtils.decodeCheckAndTag(
                      model.getRecipient(),
                      Arrays.asList(
                          ApiIdentifiers.ACCOUNT_PUBKEY,
                          ApiIdentifiers.CONTRACT_PUBKEY,
                          ApiIdentifiers.ORACLE_PUBKEY,
                          ApiIdentifiers.NAME));
              rlpWriter.writeByteArray(senderWithTag);
              rlpWriter.writeByteArray(recipientWithTag);
              this.checkZeroAndWriteValue(rlpWriter, model.getAmount());
              this.checkZeroAndWriteValue(rlpWriter, model.getFee());
              this.checkZeroAndWriteValue(rlpWriter, model.getTtl());
              this.checkZeroAndWriteValue(rlpWriter, model.getNonce());
              rlpWriter.writeByteArray(model.getPayload().getBytes());
            });
    return encodedRlp;
  }
}
