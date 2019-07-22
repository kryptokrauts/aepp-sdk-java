package com.kryptokrauts.aeternity.sdk.service.transaction.type.impl;

import com.kryptokrauts.aeternity.generated.api.rxjava.OracleApi;
import com.kryptokrauts.aeternity.generated.model.OracleResponseTx;
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
public class OracleResponseTransaction extends AbstractTransaction<OracleResponseTx> {

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
  protected OracleResponseTx toModel() {
    OracleResponseTx oracleResponseTx = new OracleResponseTx();
    oracleResponseTx.fee(this.fee);
    oracleResponseTx.nonce(this.nonce);
    oracleResponseTx.oracleId(this.oracleId);
    oracleResponseTx.queryId(this.queryId);
    oracleResponseTx.response(this.response);
    oracleResponseTx.responseTtl(this.responseTtl);
    oracleResponseTx.ttl(this.ttl);
    return oracleResponseTx;
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
