package com.kryptokrauts.aeternity.sdk.service.transaction.type.impl;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.constants.SerializationTags;
import com.kryptokrauts.aeternity.sdk.service.transaction.fee.FeeCalculationModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.fee.impl.OracleFeeCalculationModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.OracleQueryTransactionModel;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import io.reactivex.Single;
import java.math.BigInteger;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.rlp.RLP;

@SuperBuilder
@ToString
public class OracleQueryTransaction extends AbstractTransaction<OracleQueryTransactionModel> {

  @NonNull private ExternalApi externalApi;

  @Override
  protected Single<UnsignedTx> createInternal() {
    return this.externalApi.rxPostOracleQuery(model.toApiModel());
  }

  @Override
  protected Bytes createRLPEncodedList() {
    Bytes encodedRlp =
        RLP.encodeList(
            rlpWriter -> {
              rlpWriter.writeInt(SerializationTags.OBJECT_TAG_ORACLE_QUERY_TRANSACTION);
              rlpWriter.writeInt(SerializationTags.VSN_1);
              byte[] senderIdWithTag =
                  EncodingUtils.decodeCheckAndTag(
                      model.getSenderId(), SerializationTags.ID_TAG_ACCOUNT);
              rlpWriter.writeByteArray(senderIdWithTag);
              this.checkZeroAndWriteValue(rlpWriter, model.getNonce());
              byte[] oracleIdWithTag =
                  EncodingUtils.decodeCheckAndTag(
                      model.getOracleId(), SerializationTags.ID_TAG_ORACLE);
              rlpWriter.writeByteArray(oracleIdWithTag);
              rlpWriter.writeByteArray(model.getQuery().getBytes());
              this.checkZeroAndWriteValue(rlpWriter, model.getQueryFee());
              switch (model.getQueryTtlType()) {
                case DELTA:
                  this.checkZeroAndWriteValue(rlpWriter, BigInteger.ZERO);
                  break;
                case BLOCK:
                  this.checkZeroAndWriteValue(rlpWriter, BigInteger.ONE);
              }
              this.checkZeroAndWriteValue(rlpWriter, model.getQueryTtl());
              this.checkZeroAndWriteValue(rlpWriter, BigInteger.ZERO);
              this.checkZeroAndWriteValue(rlpWriter, model.getResponseTtl());
              this.checkZeroAndWriteValue(rlpWriter, model.getFee());
              this.checkZeroAndWriteValue(rlpWriter, model.getTtl());
            });
    return encodedRlp;
  }

  @Override
  protected FeeCalculationModel getFeeCalculationModel() {
    return new OracleFeeCalculationModel();
  }
}
