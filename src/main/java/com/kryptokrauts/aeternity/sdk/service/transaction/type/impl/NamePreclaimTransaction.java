package com.kryptokrauts.aeternity.sdk.service.transaction.type.impl;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.constants.SerializationTags;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.NamePreclaimTransactionModel;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import io.reactivex.Single;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.rlp.RLP;

@SuperBuilder
@ToString
public class NamePreclaimTransaction extends AbstractTransaction<NamePreclaimTransactionModel> {

  @NonNull private ExternalApi externalApi;

  @Override
  protected Single<UnsignedTx> createInternal() {
    return externalApi.rxPostNamePreclaim(model.toApiModel());
  }

  @Override
  protected Bytes createRLPEncodedList() {
    Bytes encodedRlp =
        RLP.encodeList(
            rlpWriter -> {
              rlpWriter.writeInt(SerializationTags.OBJECT_TAG_NAME_SERVICE_PRECLAIM_TRANSACTION);
              rlpWriter.writeInt(SerializationTags.VSN);
              byte[] accountIdWithTag =
                  EncodingUtils.decodeCheckAndTag(
                      model.getAccountId(), SerializationTags.ID_TAG_ACCOUNT);
              byte[] commitmentIdWithTag =
                  EncodingUtils.decodeCheckAndTag(
                      EncodingUtils.generateCommitmentHash(model.getName(), model.getSalt()),
                      SerializationTags.ID_TAG_COMMITMENT);
              rlpWriter.writeByteArray(accountIdWithTag);
              this.checkZeroAndWriteValue(rlpWriter, model.getNonce());
              rlpWriter.writeByteArray(commitmentIdWithTag);
              this.checkZeroAndWriteValue(rlpWriter, model.getFee());
              this.checkZeroAndWriteValue(rlpWriter, model.getTtl());
            });
    return encodedRlp;
  }
}
