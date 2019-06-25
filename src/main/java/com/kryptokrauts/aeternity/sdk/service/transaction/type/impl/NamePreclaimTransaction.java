package com.kryptokrauts.aeternity.sdk.service.transaction.type.impl;

import com.kryptokrauts.aeternity.generated.api.rxjava.NameServiceApi;
import com.kryptokrauts.aeternity.generated.model.NamePreclaimTx;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.constants.SerializationTags;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import com.kryptokrauts.aeternity.sdk.util.ValidationUtil;
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
  @NonNull private String name; // will be used to generate the commitmentId
  @NonNull private BigInteger salt; // will be used to generate the commitmentId
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
    namePreclaimTx.setAccountId(this.accountId);
    namePreclaimTx.setCommitmentId(EncodingUtils.generateCommitmentHash(this.name, this.salt));
    namePreclaimTx.setFee(this.fee);
    namePreclaimTx.setNonce(this.nonce);
    namePreclaimTx.setTtl(this.ttl);
    return namePreclaimTx;
  }

  @Override
  protected void validateInput() {
    ValidationUtil.checkNamespace(this.name);
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
                      EncodingUtils.generateCommitmentHash(this.name, this.salt),
                      SerializationTags.ID_TAG_COMMITMENT);
              rlpWriter.writeByteArray(accountIdWithTag);
              this.checkZeroAndWriteValue(rlpWriter, this.nonce);
              rlpWriter.writeByteArray(commitmentIdWithTag);
              this.checkZeroAndWriteValue(rlpWriter, this.fee);
              this.checkZeroAndWriteValue(rlpWriter, this.ttl);
            });
    return encodedRlp;
  }
}
