package com.kryptokrauts.aeternity.sdk.service.transaction.type.impl;

import com.kryptokrauts.aeternity.generated.api.rxjava.OracleApi;
import com.kryptokrauts.aeternity.generated.model.OracleQueryTx;
import com.kryptokrauts.aeternity.generated.model.RelativeTTL;
import com.kryptokrauts.aeternity.generated.model.TTL;
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
public class OracleQueryTransaction extends AbstractTransaction<OracleQueryTx> {

  @NonNull private String senderId;
  @NonNull private String oracleId;
  @NonNull private BigInteger nonce;
  @NonNull private String query;
  @NonNull private BigInteger queryFee;
  @NonNull private TTL queryTtl;
  @NonNull private RelativeTTL responseTtl;
  @NonNull private BigInteger ttl;
  private OracleApi oracleApi;

  @Override
  protected Single<UnsignedTx> createInternal() {
    return this.oracleApi.rxPostOracleQuery(toModel());
  }

  @Override
  protected OracleQueryTx toModel() {
    OracleQueryTx oracleQueryTx = new OracleQueryTx();
    oracleQueryTx.senderId(this.senderId);
    oracleQueryTx.oracleId(this.oracleId);
    oracleQueryTx.fee(this.fee);
    oracleQueryTx.nonce(this.nonce);
    oracleQueryTx.query(this.query);
    oracleQueryTx.queryFee(this.queryFee);
    oracleQueryTx.queryTtl(this.queryTtl);
    oracleQueryTx.responseTtl(this.responseTtl);
    return oracleQueryTx;
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
              rlpWriter.writeInt(SerializationTags.OBJECT_TAG_ORACLE_QUERY_TRANSACTION);
              rlpWriter.writeInt(SerializationTags.VSN);
              byte[] senderIdWithTag =
                  EncodingUtils.decodeCheckAndTag(this.senderId, SerializationTags.ID_TAG_ACCOUNT);
              rlpWriter.writeByteArray(senderIdWithTag);
              this.checkZeroAndWriteValue(rlpWriter, this.nonce);
              byte[] oracleIdWithTag =
                  EncodingUtils.decodeCheckAndTag(this.oracleId, SerializationTags.ID_TAG_ORACLE);
              rlpWriter.writeByteArray(oracleIdWithTag);
              rlpWriter.writeByteArray(this.query.getBytes());
              this.checkZeroAndWriteValue(rlpWriter, this.queryFee);
              switch (this.queryTtl.getType()) {
                case DELTA:
                  this.checkZeroAndWriteValue(rlpWriter, BigInteger.ZERO);
                  break;
                case BLOCK:
                  this.checkZeroAndWriteValue(rlpWriter, BigInteger.ONE);
              }
              this.checkZeroAndWriteValue(rlpWriter, this.queryTtl.getValue());
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
