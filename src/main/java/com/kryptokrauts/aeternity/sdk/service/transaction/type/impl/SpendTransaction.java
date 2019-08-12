package com.kryptokrauts.aeternity.sdk.service.transaction.type.impl;

import com.kryptokrauts.aeternity.generated.api.rxjava.TransactionApi;
import com.kryptokrauts.aeternity.generated.model.SpendTx;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.constants.SerializationTags;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import io.reactivex.Single;
import java.math.BigInteger;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.rlp.RLP;

@Getter
@SuperBuilder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SpendTransaction extends AbstractTransaction<SpendTx> {

  @EqualsAndHashCode.Include @NonNull private String sender;
  @EqualsAndHashCode.Include @NonNull private String recipient;
  @EqualsAndHashCode.Include @NonNull private BigInteger amount;
  @EqualsAndHashCode.Include @NonNull private String payload;
  @EqualsAndHashCode.Include @NonNull private BigInteger ttl;
  @EqualsAndHashCode.Include @NonNull private BigInteger nonce;
  private TransactionApi transactionApi;

  @Override
  protected Single<UnsignedTx> createInternal() {
    return transactionApi.rxPostSpend(toModel());
  }

  @Override
  protected SpendTx toModel() {
    SpendTx spendTx = new SpendTx();
    spendTx.setSenderId(sender);
    spendTx.setRecipientId(recipient);
    spendTx.setAmount(amount);
    spendTx.setPayload(payload);
    spendTx.setFee(fee);
    spendTx.setTtl(ttl);
    spendTx.setNonce(nonce);

    return spendTx;
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
              rlpWriter.writeInt(SerializationTags.OBJECT_TAG_SPEND_TRANSACTION);
              rlpWriter.writeInt(SerializationTags.VSN);
              byte[] senderWithTag =
                  EncodingUtils.decodeCheckAndTag(this.sender, SerializationTags.ID_TAG_ACCOUNT);
              byte[] recipientWithTag =
                  EncodingUtils.decodeCheckAndTag(this.recipient, SerializationTags.ID_TAG_ACCOUNT);
              rlpWriter.writeByteArray(senderWithTag);
              rlpWriter.writeByteArray(recipientWithTag);
              this.checkZeroAndWriteValue(rlpWriter, this.amount);
              this.checkZeroAndWriteValue(rlpWriter, this.fee);
              this.checkZeroAndWriteValue(rlpWriter, this.ttl);
              this.checkZeroAndWriteValue(rlpWriter, this.nonce);
              rlpWriter.writeByteArray(this.payload.getBytes());
            });
    return encodedRlp;
  }
}
