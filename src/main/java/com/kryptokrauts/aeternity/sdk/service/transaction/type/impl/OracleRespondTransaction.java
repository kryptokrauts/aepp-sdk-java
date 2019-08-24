package com.kryptokrauts.aeternity.sdk.service.transaction.type.impl;

import java.math.BigInteger;

import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.rlp.RLP;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.constants.SerializationTags;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.OracleRespondTransactionModel;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;

import io.reactivex.Single;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@ToString
public class OracleRespondTransaction extends AbstractTransaction<OracleRespondTransactionModel> {

	@NonNull
	private ExternalApi externalApi;

	@Override
	protected Single<UnsignedTx> createInternal() {
		return this.externalApi.rxPostOracleRespond(model.toApiModel());
	}

	@Override
	protected Bytes createRLPEncodedList() {
		Bytes encodedRlp = RLP.encodeList(rlpWriter -> {
			rlpWriter.writeInt(SerializationTags.OBJECT_TAG_ORACLE_RESPONSE_TRANSACTION);
			rlpWriter.writeInt(SerializationTags.VSN);
			byte[] oracleIdWithTag = EncodingUtils.decodeCheckAndTag(model.getOracleId(),
					SerializationTags.ID_TAG_ORACLE);
			rlpWriter.writeByteArray(oracleIdWithTag);
			this.checkZeroAndWriteValue(rlpWriter, model.getNonce());
			rlpWriter.writeByteArray(EncodingUtils.decodeCheckWithIdentifier(model.getQueryId()));
			rlpWriter.writeByteArray(model.getResponse().getBytes());
			switch (model.getResponseTtl().getType()) {
			case DELTA:
				this.checkZeroAndWriteValue(rlpWriter, BigInteger.ZERO);
				break;
			}
			this.checkZeroAndWriteValue(rlpWriter, model.getResponseTtl().getValue());
			this.checkZeroAndWriteValue(rlpWriter, model.getFee());
			this.checkZeroAndWriteValue(rlpWriter, model.getTtl());
		});
		return encodedRlp;
	}
}
