package com.kryptokrauts.aeternity.sdk.service.transaction.type.impl;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.constants.ApiIdentifiers;
import com.kryptokrauts.aeternity.sdk.constants.SerializationTags;
import com.kryptokrauts.aeternity.sdk.service.transaction.fee.FeeCalculationModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.fee.impl.GaMetaFeeCalculationModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.GeneralizedAccountsMetaTransactionModel;
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
public class GeneralizedAccountsMetaTransaction
    extends AbstractTransactionWithInnerTx<GeneralizedAccountsMetaTransactionModel> {

  @NonNull private ExternalApi externalApi;

  @Override
  protected Single<UnsignedTx> createInternal() {
    throw new UnsupportedOperationException("GAMetaTx cannot be created using the debug API.");
  }

  /**
   * for ga transaction the ga tx is wrapped into a signed wrapper as well as the actual transaction
   * to authorize
   */
  @Override
  public Bytes createRLPEncodedList() {
    Bytes encodedRlp =
        RLP.encodeList(
            rlpWriter -> {
              rlpWriter.writeInt(
                  SerializationTags.OBJECT_TAG_GENERALIZED_ACCOUNTS_META_TRANSACTION);
              rlpWriter.writeInt(SerializationTags.VSN_2);
              byte[] gaIdWithTag =
                  EncodingUtils.decodeCheckAndTag(
                      model.getGaId(), Arrays.asList(ApiIdentifiers.ACCOUNT_PUBKEY));
              rlpWriter.writeByteArray(gaIdWithTag);
              rlpWriter.writeByteArray(
                  EncodingUtils.decodeCheckWithIdentifier(model.getAuthData()));
              this.checkZeroAndWriteValue(rlpWriter, model.getVirtualMachine().getAbiVersion());
              this.checkZeroAndWriteValue(rlpWriter, model.getFee());
              this.checkZeroAndWriteValue(rlpWriter, model.getGasLimit());
              this.checkZeroAndWriteValue(rlpWriter, model.getGasPrice());
              rlpWriter.writeValue(wrapSignedTransactionForGA(this.innerTxRLPEncodedList));
            });
    return wrapSignedTransactionForGA(encodedRlp);
  }

  /**
   * wrap into a signed tx with empty list of signatures
   *
   * @param unsignedTx
   * @return
   */
  private Bytes wrapSignedTransactionForGA(Bytes unsignedTx) {
    return RLP.encodeList(
        rlpWriter -> {
          rlpWriter.writeInt(SerializationTags.OBJECT_TAG_SIGNED_TRANSACTION);
          rlpWriter.writeInt(SerializationTags.VSN_1);
          rlpWriter.writeList(writer -> {});
          rlpWriter.writeValue(unsignedTx);
        });
  }

  @Override
  protected FeeCalculationModel getFeeCalculationModel() {
    return new GaMetaFeeCalculationModel();
  }
}
