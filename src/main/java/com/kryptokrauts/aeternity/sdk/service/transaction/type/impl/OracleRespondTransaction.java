package com.kryptokrauts.aeternity.sdk.service.transaction.type.impl;

import com.kryptokrauts.aeternity.generated.api.rxjava.OracleApi;
import com.kryptokrauts.aeternity.generated.model.OracleRespondTx;
import com.kryptokrauts.aeternity.generated.model.RelativeTTL;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.constants.SerializationTags;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import io.reactivex.Single;
import java.math.BigInteger;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.rlp.RLP;

@Getter
@SuperBuilder
@ToString
public class OracleRespondTransaction extends AbstractTransaction<OracleRespondTx> {

  @NonNull private String senderId;
  @NonNull private String oracleId;
  @NonNull private String queryId;
  @NonNull private BigInteger nonce;
  @NonNull private String response;
  @NonNull private RelativeTTL responseTtl;
  @NonNull private String responseFormat;
  @NonNull private BigInteger ttl;
  private OracleApi oracleApi;

  @Override
  protected Single<UnsignedTx> createInternal() {
    return this.oracleApi.rxPostOracleRespond(toModel());
  }

  @Override
  protected OracleRespondTx toModel() {
    OracleRespondTx oracleRespondTx = new OracleRespondTx();
    oracleRespondTx.fee(this.fee);
    oracleRespondTx.nonce(this.nonce);
    oracleRespondTx.oracleId(this.oracleId);
    oracleRespondTx.queryId(this.queryId);
    oracleRespondTx.response(this.response);
    oracleRespondTx.responseTtl(this.responseTtl);
    oracleRespondTx.ttl(this.ttl);
    return oracleRespondTx;
  }

  @Override
  protected void validateInput() {
    // nothing to validate here
  }

  @Override
  protected Bytes createRLPEncodedList() {
    Bytes encodedRlp =
        RLP.encodeList(
            rlpWriter -> {
              rlpWriter.writeInt(SerializationTags.OBJECT_TAG_ORACLE_RESPONSE_TRANSACTION);
              rlpWriter.writeInt(SerializationTags.VSN);
              byte[] oracleIdWithTag =
                  EncodingUtils.decodeCheckAndTag(this.oracleId, SerializationTags.ID_TAG_ORACLE);
              rlpWriter.writeByteArray(oracleIdWithTag);
              this.checkZeroAndWriteValue(rlpWriter, this.nonce);
              rlpWriter.writeByteArray(EncodingUtils.decodeCheckWithIdentifier(this.queryId));
              rlpWriter.writeByteArray(this.response.getBytes());
              switch (this.responseTtl.getType()) {
                case DELTA:
                  this.checkZeroAndWriteValue(rlpWriter, BigInteger.ZERO);
                  break;
              }
              this.checkZeroAndWriteValue(rlpWriter, this.responseTtl.getValue());
              this.checkZeroAndWriteValue(rlpWriter, this.fee);
              this.checkZeroAndWriteValue(rlpWriter, this.ttl);
            });
    return encodedRlp;
  }
}
