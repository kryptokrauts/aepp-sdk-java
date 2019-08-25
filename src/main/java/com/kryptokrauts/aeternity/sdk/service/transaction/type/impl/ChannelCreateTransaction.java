package com.kryptokrauts.aeternity.sdk.service.transaction.type.impl;

import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.rlp.RLP;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.constants.SerializationTags;
import com.kryptokrauts.aeternity.sdk.service.transaction.fee.FeeCalculationModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.fee.impl.ChannelForceProgressFeeCalculationModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.ChannelCreateTransactionModel;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;

import io.reactivex.Single;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@ToString
public class ChannelCreateTransaction extends AbstractTransaction<ChannelCreateTransactionModel> {

	@NonNull
	private ExternalApi externalApi;

	@Override
	protected Single<UnsignedTx> createInternal() {
		return externalApi.rxPostChannelCreate(model.toApiModel());
	}

	@Override
	protected Bytes createRLPEncodedList() {
		Bytes encodedRlp = RLP.encodeList(rlpWriter -> {
			rlpWriter.writeInt(SerializationTags.OBJECT_TAG_CHANNEL_CREATE_TRANSACTION);
			rlpWriter.writeInt(SerializationTags.VSN);
			byte[] initiatorIdWithTag = EncodingUtils.decodeCheckAndTag(model.getInitiator(),
					SerializationTags.ID_TAG_ACCOUNT);
			rlpWriter.writeByteArray(initiatorIdWithTag);
			this.checkZeroAndWriteValue(rlpWriter, model.getInitiatorAmount());
			byte[] responderIdWithTag = EncodingUtils.decodeCheckAndTag(model.getResponder(),
					SerializationTags.ID_TAG_ACCOUNT);
			rlpWriter.writeByteArray(responderIdWithTag);
			this.checkZeroAndWriteValue(rlpWriter, model.getResponderAmount());
			this.checkZeroAndWriteValue(rlpWriter, model.getChannelReserve());
			this.checkZeroAndWriteValue(rlpWriter, model.getLockPeriod());
			this.checkZeroAndWriteValue(rlpWriter, model.getTtl());
			this.checkZeroAndWriteValue(rlpWriter, model.getFee());
			rlpWriter.writeString(model.getStateHash());
			this.checkZeroAndWriteValue(rlpWriter, model.getNonce());
		});
		return encodedRlp;
	}

	@Override
	protected FeeCalculationModel getFeeCalculationModel() {
		return new ChannelForceProgressFeeCalculationModel();
	}
}
