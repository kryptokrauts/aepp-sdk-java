package com.kryptokrauts.aeternity.sdk.service.transaction.type.impl;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.constants.ApiIdentifiers;
import com.kryptokrauts.aeternity.sdk.constants.SerializationTags;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.SpendTransactionModel;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import io.reactivex.Single;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.rlp.RLP;

@SuperBuilder
@ToString
public class SpendTransaction extends AbstractTransaction<SpendTransactionModel> {

  @NonNull private ExternalApi externalApi;

  @Override
  protected Single<UnsignedTx> createInternal() {
    return externalApi.rxPostSpend(model.toApiModel());
  }

  @Override
  protected Bytes createRLPEncodedList() {
    Bytes encodedRlp =
        RLP.encodeList(
            rlpWriter -> {
              rlpWriter.writeInt(SerializationTags.OBJECT_TAG_SPEND_TRANSACTION);
              rlpWriter.writeInt(SerializationTags.VSN_1);
              byte[] senderWithTag =
                  EncodingUtils.decodeCheckAndTag(
                      model.getSender(), SerializationTags.ID_TAG_ACCOUNT);
              byte[] recipientWithTag =
                  EncodingUtils.decodeCheckAndTag(
                      model.getRecipient(),
                      this.determineSerializationTagOfRecipient(model.getRecipient()));
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

  private int determineSerializationTagOfRecipient(String recipient) {
    String[] splitted = recipient.split("_");
    if (splitted.length != 2) {
      throw new IllegalArgumentException("input has wrong format");
    }
    switch (splitted[0]) {
      case ApiIdentifiers.ACCOUNT_PUBKEY:
        return SerializationTags.ID_TAG_ACCOUNT;
      case ApiIdentifiers.CONTRACT_PUBKEY:
        return SerializationTags.ID_TAG_CONTRACT;
      case ApiIdentifiers.ORACLE_PUBKEY:
        return SerializationTags.ID_TAG_ORACLE;
      case ApiIdentifiers.NAME:
        return SerializationTags.ID_TAG_NAME;
      default:
        throw new IllegalArgumentException("illegal identifier: " + splitted[0]);
    }
  }
}
