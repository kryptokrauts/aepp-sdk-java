package com.kryptokrauts.aeternity.sdk.service.transaction.type.impl;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.api.rxjava.InternalApi;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.constants.ApiIdentifiers;
import com.kryptokrauts.aeternity.sdk.constants.SerializationTags;
import com.kryptokrauts.aeternity.sdk.service.transaction.fee.FeeCalculationModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.fee.impl.OracleFeeCalculationModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.OracleRespondTransactionModel;
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
public class OracleRespondTransaction extends AbstractTransaction<OracleRespondTransactionModel> {

  @NonNull private ExternalApi externalApi;
  @NonNull private InternalApi internalApi;

  @Override
  protected Single<UnsignedTx> createInternal() {
    return this.internalApi.rxPostOracleRespond(model.toApiModel(), false, null);
  }

  @Override
  public Bytes createRLPEncodedList() {
    Bytes encodedRlp =
        RLP.encodeList(
            rlpWriter -> {
              rlpWriter.writeInt(SerializationTags.OBJECT_TAG_ORACLE_RESPONSE_TRANSACTION);
              rlpWriter.writeInt(SerializationTags.VSN_1);
              byte[] oracleIdWithTag =
                  EncodingUtils.decodeCheckAndTag(
                      model.getOracleId(), Arrays.asList(ApiIdentifiers.ORACLE_PUBKEY));
              rlpWriter.writeByteArray(oracleIdWithTag);
              this.checkZeroAndWriteValue(rlpWriter, model.getNonce());
              rlpWriter.writeByteArray(EncodingUtils.decodeCheckWithIdentifier(model.getQueryId()));
              rlpWriter.writeByteArray(model.getResponse().getBytes());
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
