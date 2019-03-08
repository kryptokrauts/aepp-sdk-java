package com.kryptokrauts.aeternity.sdk.service.transaction.type.impl;

import com.kryptokrauts.aeternity.generated.api.rxjava.TransactionApi;
import com.kryptokrauts.aeternity.generated.model.SpendTx;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.constants.SerializationTags;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import io.reactivex.Single;
import java.math.BigInteger;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import net.consensys.cava.bytes.Bytes;
import net.consensys.cava.rlp.RLP;

@Getter
@SuperBuilder
public class SpendTransaction extends AbstractTransaction<SpendTx> {

  @NonNull private String sender;
  @NonNull private String recipient;
  @NonNull private BigInteger amount;
  @NonNull private String payload;
  @NonNull private BigInteger ttl;
  @NonNull private BigInteger nonce;
  @NonNull private TransactionApi transactionApi;

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
              rlpWriter.writeBigInteger(this.amount);
              rlpWriter.writeBigInteger(this.fee);
              rlpWriter.writeBigInteger(this.ttl);
              rlpWriter.writeBigInteger(this.nonce);
              rlpWriter.writeString(this.payload);
            });
    return encodedRlp;
  }
}
