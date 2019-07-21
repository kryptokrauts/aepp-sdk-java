package com.kryptokrauts.aeternity.sdk.service.transaction.type.impl;

import com.kryptokrauts.aeternity.generated.api.rxjava.OracleApi;
import com.kryptokrauts.aeternity.generated.model.OracleExtendTx;
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
public class OracleExtendTransaction extends AbstractTransaction<OracleExtendTx> {

  @NonNull private BigInteger nonce;
  @NonNull private String oracleId;
  @NonNull private RelativeTTL oracleRelativeTtl;
  @NonNull private BigInteger ttl;
  @NonNull private OracleApi oracleApi;

  @Override
  protected Single<UnsignedTx> createInternal() {
    return this.oracleApi.rxPostOracleExtend(toModel());
  }

  @Override
  protected OracleExtendTx toModel() {
    OracleExtendTx oracleExtendTx = new OracleExtendTx();
    oracleExtendTx.setFee(this.fee);
    oracleExtendTx.setNonce(this.nonce);
    oracleExtendTx.setOracleId(this.oracleId);
    oracleExtendTx.setOracleTtl(this.oracleRelativeTtl);
    oracleExtendTx.setTtl(this.ttl);
    return oracleExtendTx;
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
              rlpWriter.writeInt(SerializationTags.OBJECT_TAG_ORACLE_EXTEND_TRANSACTION);
              rlpWriter.writeInt(SerializationTags.VSN);
              byte[] oracleIdWithTag =
                  EncodingUtils.decodeCheckAndTag(this.oracleId, SerializationTags.ID_TAG_ORACLE);
              rlpWriter.writeByteArray(oracleIdWithTag);
              this.checkZeroAndWriteValue(rlpWriter, this.nonce);
              switch (this.oracleRelativeTtl.getType()) {
                case DELTA:
                  this.checkZeroAndWriteValue(rlpWriter, BigInteger.ZERO);
                  break;
              }
              this.checkZeroAndWriteValue(rlpWriter, this.oracleRelativeTtl.getValue());
              this.checkZeroAndWriteValue(rlpWriter, this.fee);
              this.checkZeroAndWriteValue(rlpWriter, this.ttl);
            });
    return encodedRlp;
  }
}
