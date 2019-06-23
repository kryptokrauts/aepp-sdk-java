package com.kryptokrauts.aeternity.sdk.service.transaction.type.impl;

import com.kryptokrauts.aeternity.generated.api.rxjava.NameServiceApi;
import com.kryptokrauts.aeternity.generated.model.NamePreclaimTx;
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
public class NamePreclaimTransaction extends AbstractTransaction<NamePreclaimTx> {

  @NonNull private String accountId;
  @NonNull private String commitmentId;
  @NonNull private BigInteger nonce;
  @NonNull private BigInteger ttl;
  @NonNull private NameServiceApi nameServiceApi;

  @Override
  protected Single<UnsignedTx> createInternal() {
    return nameServiceApi.rxPostNamePreclaim(getApiModel());
  }

  @Override
  protected NamePreclaimTx toModel() {
    NamePreclaimTx namePreclaimTx = new NamePreclaimTx();
    namePreclaimTx.setAccountId(accountId);
    namePreclaimTx.setCommitmentId(commitmentId);
    namePreclaimTx.setFee(fee);
    namePreclaimTx.setNonce(nonce);
    namePreclaimTx.setTtl(ttl);
    return namePreclaimTx;
  }

  @Override
  protected Bytes createRLPEncodedList() {
    Bytes encodedRlp =
        RLP.encodeList(
            rlpWriter -> {
              rlpWriter.writeInt(SerializationTags.OBJECT_TAG_NAME_SERVICE_PRECLAIM_TRANSACTION);
              rlpWriter.writeInt(SerializationTags.VSN);
              byte[] accountIdWithTag =
                  EncodingUtils.decodeCheckAndTag(this.accountId, SerializationTags.ID_TAG_ACCOUNT);
              byte[] commitmentIdWithTag =
                  EncodingUtils.decodeCheckAndTag(
                      this.commitmentId, SerializationTags.ID_TAG_COMMITMENT);
              rlpWriter.writeByteArray(accountIdWithTag);
              rlpWriter.writeByteArray(this.nonce.toByteArray());
              rlpWriter.writeByteArray(commitmentIdWithTag);
              rlpWriter.writeByteArray(this.fee.toByteArray());
              rlpWriter.writeByteArray(this.ttl.toByteArray());
            });
    return encodedRlp;
  }
}
