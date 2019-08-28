package com.kryptokrauts.aeternity.sdk.service.transaction.type.impl;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.constants.SerializationTags;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.NameRevokeTransactionModel;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import io.reactivex.Single;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.rlp.RLP;

@SuperBuilder
@ToString
public class NameRevokeTransaction extends AbstractTransaction<NameRevokeTransactionModel> {

  @NonNull private ExternalApi externalApi;

  @Override
  protected Single<UnsignedTx> createInternal() {
    return this.externalApi.rxPostNameRevoke(model.toApiModel());
  }

  @Override
  protected Bytes createRLPEncodedList() {
    Bytes encodedRlp =
        RLP.encodeList(
            rlpWriter -> {
              rlpWriter.writeInt(SerializationTags.OBJECT_TAG_NAME_SERVICE_REVOKE_TRANSACTION);
              rlpWriter.writeInt(SerializationTags.VSN);
              byte[] accountIdWithTag =
                  EncodingUtils.decodeCheckAndTag(
                      model.getAccountId(), SerializationTags.ID_TAG_ACCOUNT);
              rlpWriter.writeByteArray(accountIdWithTag);
              this.checkZeroAndWriteValue(rlpWriter, model.getNonce());
              byte[] nameIdWithTag =
                  EncodingUtils.decodeCheckAndTag(model.getNameId(), SerializationTags.ID_TAG_NAME);
              rlpWriter.writeByteArray(nameIdWithTag);
              this.checkZeroAndWriteValue(rlpWriter, model.getFee());
              this.checkZeroAndWriteValue(rlpWriter, model.getTtl());
            });
    return encodedRlp;
  }
}
