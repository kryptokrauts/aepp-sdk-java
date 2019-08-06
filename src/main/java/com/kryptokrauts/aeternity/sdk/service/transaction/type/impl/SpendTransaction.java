package com.kryptokrauts.aeternity.sdk.service.transaction.type.impl;

import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.rlp.RLP;

import com.kryptokrauts.aeternity.generated.api.rxjava.TransactionApi;
import com.kryptokrauts.aeternity.generated.model.SpendTx;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.constants.SerializationTags;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.AbstractTransactionModel;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;

import io.reactivex.Single;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
//@Builder(toBuilder = true, builderMethodName = "setParameters", buildMethodName = "wrap")
//@SuperBuilder(toBuilder = true, builderMethodName = "setParameters", buildMethodName = "wrap")
//@AllArgsConstructor
public class SpendTransaction extends AbstractTransaction<AbstractTransactionModel> {

//	@NonNull
//	private String sender;
//	@NonNull
//	private String recipient;
//	@NonNull
//	private BigInteger amount;
//
//	private String payload;
//	@NonNull
//	private BigInteger ttl;
//	@NonNull
//	private BigInteger nonce;
	@NonNull
	private TransactionApi transactionApi;

	@Override
	protected Single<UnsignedTx> createInternal() {
		return transactionApi.rxPostSpend(toModel());
	}

	@Override
	protected SpendTx toModel() {
		SpendTx spendTx = new SpendTx();
		spendTx.setSenderId(getTxModel().getSender());
		spendTx.setRecipientId(getTxModel().getRecipient());
		spendTx.setAmount(getTxModel().getAmount());
		spendTx.setPayload(getTxModel().getPayload());
		spendTx.setFee(getTxModel().getFee());
		spendTx.setTtl(getTxModel().getTtl());
		spendTx.setNonce(getTxModel().getNonce());

		return spendTx;
	}

	@Override
	protected void validateInput() {
		// nothing to validate here
	}

	@Override
	protected Bytes createRLPEncodedList() {
		Bytes encodedRlp = RLP.encodeList(rlpWriter -> {
			rlpWriter.writeInt(SerializationTags.OBJECT_TAG_SPEND_TRANSACTION);
			rlpWriter.writeInt(SerializationTags.VSN);
			byte[] senderWithTag = EncodingUtils.decodeCheckAndTag(getTxModel().getSender(),
					SerializationTags.ID_TAG_ACCOUNT);
			byte[] recipientWithTag = EncodingUtils.decodeCheckAndTag(getTxModel().getRecipient(),
					SerializationTags.ID_TAG_ACCOUNT);
			rlpWriter.writeByteArray(senderWithTag);
			rlpWriter.writeByteArray(recipientWithTag);
			this.checkZeroAndWriteValue(rlpWriter, getTxModel().getAmount());
			this.checkZeroAndWriteValue(rlpWriter, getTxModel().getFee());
			this.checkZeroAndWriteValue(rlpWriter, getTxModel().getTtl());
			this.checkZeroAndWriteValue(rlpWriter, getTxModel().getNonce());
			rlpWriter.writeByteArray(getTxModel().getPayload().getBytes());
		});
		return encodedRlp;
	}
}
