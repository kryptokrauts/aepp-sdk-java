package com.kryptokrauts.aeternity.sdk.service.transaction.type.impl;

import com.kryptokrauts.aeternity.generated.api.rxjava.OracleApi;
import com.kryptokrauts.aeternity.generated.model.OracleRegisterTx;
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
public class OracleRegisterTransaction extends AbstractTransaction<OracleRegisterTx> {

  @NonNull private String accountId;
  @NonNull private BigInteger abiVersion;
  @NonNull private BigInteger nonce;
  @NonNull private TTL oracleTtl;
  @NonNull private BigInteger queryFee;
  @NonNull private String queryFormat;
  @NonNull private String responseFormat;
  @NonNull private BigInteger ttl;
  @NonNull private OracleApi oracleApi;

  @Override
  protected Single<UnsignedTx> createInternal() {
    return this.oracleApi.rxPostOracleRegister(toModel());
  }

  @Override
  protected OracleRegisterTx toModel() {
    OracleRegisterTx oracleRegisterTx = new OracleRegisterTx();
    oracleRegisterTx.setAbiVersion(this.abiVersion);
    oracleRegisterTx.setAccountId(this.accountId);
    oracleRegisterTx.setFee(this.fee);
    oracleRegisterTx.setNonce(this.nonce);
    oracleRegisterTx.setOracleTtl(this.oracleTtl);
    oracleRegisterTx.setQueryFee(this.queryFee);
    oracleRegisterTx.setQueryFormat(this.queryFormat);
    oracleRegisterTx.setResponseFormat(this.responseFormat);
    oracleRegisterTx.setTtl(this.ttl);
    return oracleRegisterTx;
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
              rlpWriter.writeInt(SerializationTags.OBJECT_TAG_ORACLE_REGISTER_TRANSACTION);
              rlpWriter.writeInt(SerializationTags.VSN);
              byte[] accountIdWithTag =
                  EncodingUtils.decodeCheckAndTag(this.accountId, SerializationTags.ID_TAG_ACCOUNT);
              rlpWriter.writeByteArray(accountIdWithTag);
              this.checkZeroAndWriteValue(rlpWriter, this.nonce);
              rlpWriter.writeByteArray(this.queryFormat.getBytes());
              rlpWriter.writeByteArray(this.responseFormat.getBytes());
              this.checkZeroAndWriteValue(rlpWriter, this.queryFee);
              switch (this.oracleTtl.getType()) {
                case DELTA:
                  this.checkZeroAndWriteValue(rlpWriter, BigInteger.ZERO);
                  break;
                case BLOCK:
                  this.checkZeroAndWriteValue(rlpWriter, BigInteger.ONE);
              }
              this.checkZeroAndWriteValue(rlpWriter, this.oracleTtl.getValue());
              this.checkZeroAndWriteValue(rlpWriter, this.fee);
              this.checkZeroAndWriteValue(rlpWriter, this.ttl);
              this.checkZeroAndWriteValue(rlpWriter, this.abiVersion);
            });
    return encodedRlp;
  }
}
