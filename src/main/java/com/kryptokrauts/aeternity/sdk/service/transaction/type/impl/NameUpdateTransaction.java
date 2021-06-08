package com.kryptokrauts.aeternity.sdk.service.transaction.type.impl;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.constants.ApiIdentifiers;
import com.kryptokrauts.aeternity.sdk.constants.SerializationTags;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.NameUpdateTransactionModel;
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
public class NameUpdateTransaction extends AbstractTransaction<NameUpdateTransactionModel> {

  @NonNull private ExternalApi externalApi;

  @Override
  protected Single<UnsignedTx> createInternal() {
    return this.externalApi.rxPostNameUpdate(model.toApiModel());
  }

  @Override
  public Bytes createRLPEncodedList() {
    Bytes encodedRlp =
        RLP.encodeList(
            rlpWriter -> {
              rlpWriter.writeInt(SerializationTags.OBJECT_TAG_NAME_SERVICE_UPDATE_TRANSACTION);
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
              this.checkZeroAndWriteValue(rlpWriter, model.getNameTtl());
              rlpWriter.writeList(
                  model.getGeneratedPointers(),
                  (writer, namePointer) -> {
                    writer.writeList(
                        innerWriter -> {
                          innerWriter.writeByteArray(namePointer.getKey().getBytes());
                          byte[] pointerWithTag =
                              EncodingUtils.decodeCheckAndTag(
                                  namePointer.getId(),
                                  Arrays.asList(
                                      ApiIdentifiers.ACCOUNT_PUBKEY,
                                      ApiIdentifiers.CONTRACT_PUBKEY,
                                      ApiIdentifiers.CHANNEL,
                                      ApiIdentifiers.ORACLE_PUBKEY));
                          innerWriter.writeByteArray(pointerWithTag);
                        });
                  });
              this.checkZeroAndWriteValue(rlpWriter, model.getClientTtl());
              this.checkZeroAndWriteValue(rlpWriter, model.getFee());
              this.checkZeroAndWriteValue(rlpWriter, model.getTtl());
            });
    return encodedRlp;
  }
}
