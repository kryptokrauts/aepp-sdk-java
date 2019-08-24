package com.kryptokrauts.aeternity.sdk.service.transaction.type.impl;

import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.rlp.RLP;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.constants.SerializationTags;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.ChannelCloseMutualTransactionModel;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;

import io.reactivex.Single;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ChannelCloseMutualTransaction extends AbstractTransaction<ChannelCloseMutualTransactionModel> {

	@NonNull
	private ExternalApi externalApi;

	@Override
	protected Single<UnsignedTx> createInternal() {
		return externalApi.rxPostChannelCloseMutual(model.toApiModel());
	}

	@Override
	protected Bytes createRLPEncodedList() {
		Bytes encodedRlp = RLP.encodeList(rlpWriter -> {
			rlpWriter.writeInt(SerializationTags.OBJECT_TAG_CHANNEL_CLOSE_MUTUAL_TRANSACTION);
			rlpWriter.writeInt(SerializationTags.VSN);
			byte[] channelIdWithTag = EncodingUtils.decodeCheckAndTag(model.getChannelId(),
					SerializationTags.ID_TAG_CHANNEL);
			byte[] fromIdWithTag = EncodingUtils.decodeCheckAndTag(model.getFromId(), SerializationTags.ID_TAG_ACCOUNT);
			rlpWriter.writeByteArray(channelIdWithTag);
			rlpWriter.writeByteArray(fromIdWithTag);
			this.checkZeroAndWriteValue(rlpWriter, model.getInitiatorAmountFinal());
			this.checkZeroAndWriteValue(rlpWriter, model.getResponderAmountFinal());
			this.checkZeroAndWriteValue(rlpWriter, model.getTtl());
			this.checkZeroAndWriteValue(rlpWriter, model.getFee());
			this.checkZeroAndWriteValue(rlpWriter, model.getNonce());
		});
		return encodedRlp;
	}
}
