package com.kryptokrauts.aeternity.sdk.service.transaction.type.impl;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.api.rxjava.InternalApi;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.constants.ApiIdentifiers;
import com.kryptokrauts.aeternity.sdk.constants.SerializationTags;
import com.kryptokrauts.aeternity.sdk.service.transaction.fee.FeeCalculationModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.fee.impl.OracleFeeCalculationModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.OracleQueryTransactionModel;
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
public class OracleQueryTransaction extends AbstractTransaction<OracleQueryTransactionModel> {

  @NonNull private ExternalApi externalApi;
  @NonNull private InternalApi internalApi;

  @Override
  protected Single<UnsignedTx> createInternal() {
    return this.internalApi.rxPostOracleQuery(model.toApiModel(), false);
  }

  @Override
  public Bytes createRLPEncodedList() {
    Bytes encodedRlp =
        RLP.encodeList(
            rlpWriter -> {
              rlpWriter.writeInt(SerializationTags.OBJECT_TAG_ORACLE_QUERY_TRANSACTION);
              rlpWriter.writeInt(SerializationTags.VSN_1);
              byte[] senderIdWithTag =
                  EncodingUtils.decodeCheckAndTag(
                      model.getSenderId(), Arrays.asList(ApiIdentifiers.ACCOUNT_PUBKEY));
              rlpWriter.writeByteArray(senderIdWithTag);
              this.checkZeroAndWriteValue(rlpWriter, model.getNonce());
              byte[] oracleIdWithTag =
                  EncodingUtils.decodeCheckAndTag(
                      model.getOracleId(), Arrays.asList(ApiIdentifiers.ORACLE_PUBKEY));
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
