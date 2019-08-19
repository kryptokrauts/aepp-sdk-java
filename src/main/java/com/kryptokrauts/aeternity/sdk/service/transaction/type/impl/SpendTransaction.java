package com.kryptokrauts.aeternity.sdk.service.transaction.type.impl;

import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.rlp.RLP;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.constants.SerializationTags;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.SpendTransactionModel;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;

import io.reactivex.Single;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class SpendTransaction extends AbstractTransaction<SpendTransactionModel> {

	@NonNull
	private ExternalApi externalApi;

	@Override
	protected Single<UnsignedTx> createInternal() {
		return externalApi.rxPostSpend(model.toApiModel());
	}

	@Override
	protected Bytes createRLPEncodedList() {
		Bytes encodedRlp = RLP.encodeList(rlpWriter -> {
			rlpWriter.writeInt(SerializationTags.OBJECT_TAG_SPEND_TRANSACTION);
			rlpWriter.writeInt(SerializationTags.VSN);
			byte[] senderWithTag = EncodingUtils.decodeCheckAndTag(model.getSender(), SerializationTags.ID_TAG_ACCOUNT);
			byte[] recipientWithTag = EncodingUtils.decodeCheckAndTag(model.getRecipient(),
					SerializationTags.ID_TAG_ACCOUNT);
			rlpWriter.writeByteArray(senderWithTag);
			rlpWriter.writeByteArray(recipientWithTag);
			this.checkZeroAndWriteValue(rlpWriter, model.getAmount());
			this.checkZeroAndWriteValue(rlpWriter, model.getFee());
			this.checkZeroAndWriteValue(rlpWriter, model.getTtl());
			this.checkZeroAndWriteValue(rlpWriter, model.getNonce());
			rlpWriter.writeByteArray(model.getPayload().getBytes());
		});
		return encodedRlp;
	}
}
