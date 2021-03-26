package com.kryptokrauts.aeternity.sdk.service.transaction.type.impl;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.constants.ApiIdentifiers;
import com.kryptokrauts.aeternity.sdk.constants.SerializationTags;
import com.kryptokrauts.aeternity.sdk.service.transaction.fee.FeeCalculationModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.fee.impl.ContractCallFeeCalculationModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
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
    extends AbstractTransaction<GeneralizedAccountsMetaTransactionModel> {

  @NonNull private ExternalApi externalApi;

  @Override
  protected Single<UnsignedTx> createInternal() {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  protected Bytes createRLPEncodedList() {
    Bytes encodedRlp =
        RLP.encodeList(
            rlpWriter -> {
              rlpWriter.writeInt(
                  SerializationTags.OBJECT_TAG_GENERALIZED_ACCOUNTS_META_TRANSACTION);
              rlpWriter.writeInt(SerializationTags.VSN_1);
              byte[] gaIdWithTag =
                  EncodingUtils.decodeCheckAndTag(
                      model.getGaId(), Arrays.asList(ApiIdentifiers.ACCOUNT_PUBKEY));
              rlpWriter.writeByteArray(gaIdWithTag);
              rlpWriter.writeByteArray(
                  EncodingUtils.decodeCheckWithIdentifier(model.getAuthData()));
              this.checkZeroAndWriteValue(rlpWriter, model.getVirtualMachine().getAbiVersion());
              this.checkZeroAndWriteValue(rlpWriter, model.getFee());
              this.checkZeroAndWriteValue(rlpWriter, model.getGas());
              this.checkZeroAndWriteValue(rlpWriter, model.getGasPrice());
              this.checkZeroAndWriteValue(rlpWriter, model.getTtl());
              rlpWriter.writeByteArray(EncodingUtils.decodeCheckWithIdentifier(model.getInnerTx()));
            });
    return encodedRlp;
  }

  @Override
  protected FeeCalculationModel getFeeCalculationModel() {
    return new ContractCallFeeCalculationModel();
  }
}
