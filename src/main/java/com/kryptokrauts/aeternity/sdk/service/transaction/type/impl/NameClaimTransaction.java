package com.kryptokrauts.aeternity.sdk.service.transaction.type.impl;

import com.kryptokrauts.aeternity.generated.api.rxjava.NameServiceApi;
import com.kryptokrauts.aeternity.generated.model.NameClaimTx;
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
public class NameClaimTransaction extends AbstractTransaction<NameClaimTx> {

  @NonNull private String accountId;
  @NonNull private BigInteger nonce;
  @NonNull private String name;
  @NonNull private BigInteger nameSalt;
  @NonNull private BigInteger ttl;
  @NonNull private NameServiceApi nameServiceApi;

  @Override
  protected Single<UnsignedTx> createInternal() {
    return nameServiceApi.rxPostNameClaim(getApiModel());
  }

  @Override
  protected NameClaimTx toModel() {
    NameClaimTx nameClaimTx = new NameClaimTx();
    nameClaimTx.setAccountId(accountId);
    nameClaimTx.setNonce(nonce);
    nameClaimTx.setName(name);
    nameClaimTx.setNameSalt(nameSalt);
    nameClaimTx.setFee(fee);
    nameClaimTx.setTtl(ttl);
    return nameClaimTx;
  }

  @Override
  protected Bytes createRLPEncodedList() {
    Bytes encodedRlp =
        RLP.encodeList(
            rlpWriter -> {
              rlpWriter.writeInt(SerializationTags.OBJECT_TAG_NAME_SERVICE_CLAIM_TRANSACTION);
              rlpWriter.writeInt(SerializationTags.VSN);
              byte[] accountIdWithTag =
                  EncodingUtils.decodeCheckAndTag(this.accountId, SerializationTags.ID_TAG_ACCOUNT);
              rlpWriter.writeByteArray(accountIdWithTag);
              rlpWriter.writeByteArray(this.nonce.toByteArray());
              rlpWriter.writeByteArray(this.name.getBytes());
              rlpWriter.writeByteArray(this.nameSalt.toByteArray());
              rlpWriter.writeByteArray(this.fee.toByteArray());
              rlpWriter.writeByteArray(this.ttl.toByteArray());
            });
    return encodedRlp;
  }
}
