package com.kryptokrauts.aeternity.sdk.service.transaction.type.impl;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.constants.SerializationTags;
import com.kryptokrauts.aeternity.sdk.service.transaction.fee.FeeCalculationModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.fee.impl.ContractCallFeeCalculationModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.ContractCallTransactionModel;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import io.reactivex.Single;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.rlp.RLP;

@SuperBuilder
@ToString
public class ContractCallTransaction extends AbstractTransaction<ContractCallTransactionModel> {

  @NonNull private ExternalApi externalApi;

  @Override
  protected Bytes createRLPEncodedList() {
    Bytes encodedRlp =
        RLP.encodeList(
            rlpWriter -> {
              rlpWriter.writeInt(SerializationTags.OBJECT_TAG_CONTRACT_CALL_TRANSACTION);
              rlpWriter.writeInt(SerializationTags.VSN);
              byte[] callerWithTag =
                  EncodingUtils.decodeCheckAndTag(
                      model.getCallerId(), SerializationTags.ID_TAG_ACCOUNT);
              rlpWriter.writeByteArray(callerWithTag);
              this.checkZeroAndWriteValue(rlpWriter, model.getNonce());
              byte[] contractWithTag =
                  EncodingUtils.decodeCheckAndTag(
                      this.model.getContractId(), SerializationTags.ID_TAG_CONTRACT);
              rlpWriter.writeByteArray(contractWithTag);
              this.checkZeroAndWriteValue(rlpWriter, model.getAbiVersion());
              this.checkZeroAndWriteValue(rlpWriter, model.getFee());
              this.checkZeroAndWriteValue(rlpWriter, model.getTtl());
              this.checkZeroAndWriteValue(rlpWriter, model.getAmount());
              this.checkZeroAndWriteValue(rlpWriter, model.getGas());
              this.checkZeroAndWriteValue(rlpWriter, model.getGasPrice());
              rlpWriter.writeByteArray(
                  EncodingUtils.decodeCheckWithIdentifier(model.getCallData()));
            });
    return encodedRlp;
  }

  @Override
  protected Single<UnsignedTx> createInternal() {
    return externalApi.rxPostContractCall(model.toApiModel());
  }

  @Override
  protected FeeCalculationModel getFeeCalculationModel() {
    return new ContractCallFeeCalculationModel();
  }
}
