package com.kryptokrauts.aeternity.sdk.service.transaction.type.impl;

import com.kryptokrauts.aeternity.generated.api.rxjava.NameServiceApi;
import com.kryptokrauts.aeternity.generated.model.NamePointer;
import com.kryptokrauts.aeternity.generated.model.NameUpdateTx;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.constants.ApiIdentifiers;
import com.kryptokrauts.aeternity.sdk.constants.SerializationTags;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import com.kryptokrauts.aeternity.sdk.util.ValidationUtil;
import io.reactivex.Single;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.rlp.RLP;

@Getter
@SuperBuilder
@ToString
public class NameUpdateTransaction extends AbstractTransaction<NameUpdateTx> {

  @NonNull private String accountId;
  @NonNull private BigInteger nonce;
  @NonNull private String nameId;
  @NonNull private BigInteger ttl;
  @NonNull private BigInteger nameTtl;
  @NonNull private BigInteger clientTtl;
  @NonNull private List<NamePointer> pointers;
  @NonNull private NameServiceApi nameServiceApi;

  @Override
  protected Single<UnsignedTx> createInternal() {
    return this.nameServiceApi.rxPostNameUpdate(getApiModel());
  }

  @Override
  protected NameUpdateTx toModel() {
    NameUpdateTx nameUpdateTx = new NameUpdateTx();
    nameUpdateTx.setAccountId(this.accountId);
    nameUpdateTx.setNonce(this.nonce);
    nameUpdateTx.setNameId(nameId);
    nameUpdateTx.setFee(this.fee);
    nameUpdateTx.setTtl(this.ttl);
    nameUpdateTx.setNameTtl(nameTtl);
    nameUpdateTx.setClientTtl(clientTtl);
    nameUpdateTx.setPointers(pointers);
    return nameUpdateTx;
  }

  @Override
  protected void validateInput() {
    // Validate parameters
    ValidationUtil.checkParameters(
        validate -> Optional.ofNullable(nameId != null),
        nameId,
        "validateUpdateTransaction",
        Arrays.asList("nameId"),
        ValidationUtil.PARAMETER_IS_NULL);
    ValidationUtil.checkParameters(
        validate -> Optional.ofNullable(nameId.startsWith(ApiIdentifiers.NAME)),
        nameId,
        "validateUpdateTransaction",
        Arrays.asList("nameId", ApiIdentifiers.NAME),
        ValidationUtil.MISSING_API_IDENTIFIER);
  }

  @Override
  protected Bytes createRLPEncodedList() {
    Bytes encodedRlp =
        RLP.encodeList(
            rlpWriter -> {
              rlpWriter.writeInt(SerializationTags.OBJECT_TAG_NAME_SERVICE_UPDATE_TRANSACTION);
              rlpWriter.writeInt(SerializationTags.VSN);
              byte[] accountIdWithTag =
                  EncodingUtils.decodeCheckAndTag(this.accountId, SerializationTags.ID_TAG_ACCOUNT);
              rlpWriter.writeByteArray(accountIdWithTag);
              this.checkZeroAndWriteValue(rlpWriter, this.nonce);
              byte[] nameIdWithTag =
                  EncodingUtils.decodeCheckAndTag(this.nameId, SerializationTags.ID_TAG_NAME);
              rlpWriter.writeByteArray(nameIdWithTag);
              this.checkZeroAndWriteValue(rlpWriter, this.nameTtl);
              rlpWriter.writeList(
                  writer -> {
                    for (NamePointer pointer : pointers) {
                      writer.writeString("account_pubkey");
                      byte[] pointerAccountIdWithTag =
                          EncodingUtils.decodeCheckAndTag(
                              pointer.getId(), SerializationTags.ID_TAG_ACCOUNT);
                      rlpWriter.writeByteArray(pointerAccountIdWithTag);
                    }
                  });
              this.checkZeroAndWriteValue(rlpWriter, this.clientTtl);
              this.checkZeroAndWriteValue(rlpWriter, this.fee);
              this.checkZeroAndWriteValue(rlpWriter, this.ttl);
            });
    return encodedRlp;
  }
}
