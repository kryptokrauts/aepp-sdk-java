package com.kryptokrauts.aeternity.sdk.service.transaction.type.impl;

import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.rlp.RLP;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.constants.SerializationTags;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.ChannelSlashTransactionModel;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;

import io.reactivex.Single;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@ToString
public class ChannelSlashTransaction extends AbstractTransaction<ChannelSlashTransactionModel> {

	@NonNull
	private ExternalApi externalApi;

	@Override
	protected Single<UnsignedTx> createInternal() {
		return externalApi.rxPostChannelSlash(model.toApiModel());
	}

	@Override
	protected Bytes createRLPEncodedList() {
		Bytes encodedRlp = RLP.encodeList(rlpWriter -> {
			rlpWriter.writeInt(SerializationTags.OBJECT_TAG_CHANNEL_SLASH_TRANSACTION);
			rlpWriter.writeInt(SerializationTags.VSN);
			byte[] channelIdWithTag = EncodingUtils.decodeCheckAndTag(model.getChannelId(),
					SerializationTags.ID_TAG_CHANNEL);
			byte[] fromIdWithTag = EncodingUtils.decodeCheckAndTag(model.getFromId(), SerializationTags.ID_TAG_ACCOUNT);
			rlpWriter.writeByteArray(channelIdWithTag);
			rlpWriter.writeByteArray(fromIdWithTag);
			rlpWriter.writeString(model.getPayload());
			rlpWriter.writeString(model.getPoi()); // TODO inform about Proof of Inclusion and how it is handled
			this.checkZeroAndWriteValue(rlpWriter, model.getTtl());
			this.checkZeroAndWriteValue(rlpWriter, model.getFee());
			this.checkZeroAndWriteValue(rlpWriter, model.getNonce());
		});
		return encodedRlp;
	}
}
