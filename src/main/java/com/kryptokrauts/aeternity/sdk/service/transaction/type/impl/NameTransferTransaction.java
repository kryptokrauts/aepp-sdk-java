package com.kryptokrauts.aeternity.sdk.service.transaction.type.impl;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.api.rxjava.InternalApi;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.constants.ApiIdentifiers;
import com.kryptokrauts.aeternity.sdk.constants.SerializationTags;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.NameTransferTransactionModel;
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
public class NameTransferTransaction extends AbstractTransaction<NameTransferTransactionModel> {

  @NonNull private ExternalApi externalApi;
  @NonNull private InternalApi internalApi;

  @Override
  protected Single<UnsignedTx> createInternal() {
    return this.internalApi.rxPostNameTransfer(model.toApiModel(), false);
  }

  @Override
  public Bytes createRLPEncodedList() {
    Bytes encodedRlp =
        RLP.encodeList(
            rlpWriter -> {
              rlpWriter.writeInt(SerializationTags.OBJECT_TAG_NAME_SERVICE_TRANSFER_TRANSACTION);
              rlpWriter.writeInt(SerializationTags.VSN_1);
              byte[] accountIdWithTag =
                  EncodingUtils.decodeCheckAndTag(
                      model.getAccountId(), Arrays.asList(ApiIdentifiers.ACCOUNT_PUBKEY));
              rlpWriter.writeByteArray(accountIdWithTag);
              this.checkZeroAndWriteValue(rlpWriter, model.getNonce());
              byte[] nameIdWithTag =
                  EncodingUtils.decodeCheckAndTag(
                      model.getNameId(), Arrays.asList(ApiIdentifiers.NAME));
              rlpWriter.writeByteArray(nameIdWithTag);
              byte[] recipientIdWithTag =
                  EncodingUtils.decodeCheckAndTag(
                      model.getRecipientId(), Arrays.asList(ApiIdentifiers.ACCOUNT_PUBKEY));
              rlpWriter.writeByteArray(recipientIdWithTag);
              this.checkZeroAndWriteValue(rlpWriter, model.getFee());
              this.checkZeroAndWriteValue(rlpWriter, model.getTtl());
            });
    return encodedRlp;
  }
}
