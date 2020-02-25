package com.kryptokrauts.aeternity.sdk.service.transaction.type.impl;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.constants.ApiIdentifiers;
import com.kryptokrauts.aeternity.sdk.constants.SerializationTags;
import com.kryptokrauts.aeternity.sdk.service.transaction.fee.FeeCalculationModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.fee.impl.OracleFeeCalculationModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.OracleRegisterTransactionModel;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import io.reactivex.Single;
import java.math.BigInteger;
import java.util.Arrays;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.rlp.RLP;

@SuperBuilder
@ToString
public class OracleRegisterTransaction extends AbstractTransaction<OracleRegisterTransactionModel> {

  @NonNull private ExternalApi externalApi;

  @Override
  protected Single<UnsignedTx> createInternal() {
    return this.externalApi.rxPostOracleRegister(model.toApiModel());
  }

  @Override
  protected Bytes createRLPEncodedList() {
    Bytes encodedRlp =
        RLP.encodeList(
            rlpWriter -> {
              rlpWriter.writeInt(SerializationTags.OBJECT_TAG_ORACLE_REGISTER_TRANSACTION);
              rlpWriter.writeInt(SerializationTags.VSN_1);
              byte[] accountIdWithTag =
                  EncodingUtils.decodeCheckAndTag(
                      model.getAccountId(), Arrays.asList(ApiIdentifiers.ACCOUNT_PUBKEY));
              rlpWriter.writeByteArray(accountIdWithTag);
              this.checkZeroAndWriteValue(rlpWriter, model.getNonce());
              rlpWriter.writeByteArray(model.getQueryFormat().getBytes());
              rlpWriter.writeByteArray(model.getResponseFormat().getBytes());
              this.checkZeroAndWriteValue(rlpWriter, model.getQueryFee());
              switch (model.getOracleTtlType()) {
                case DELTA:
                  this.checkZeroAndWriteValue(rlpWriter, BigInteger.ZERO);
                  break;
                case BLOCK:
                  this.checkZeroAndWriteValue(rlpWriter, BigInteger.ONE);
                  break;
              }
              this.checkZeroAndWriteValue(rlpWriter, model.getOracleTtl());
              this.checkZeroAndWriteValue(rlpWriter, model.getFee());
              this.checkZeroAndWriteValue(rlpWriter, model.getTtl());
              this.checkZeroAndWriteValue(rlpWriter, model.getAbiVersion());
            });
    return encodedRlp;
  }

  @Override
  protected FeeCalculationModel getFeeCalculationModel() {
    return new OracleFeeCalculationModel();
  }
}
