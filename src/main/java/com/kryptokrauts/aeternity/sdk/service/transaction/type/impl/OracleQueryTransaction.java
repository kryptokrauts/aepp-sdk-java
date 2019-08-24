package com.kryptokrauts.aeternity.sdk.service.transaction.type.impl;

import java.math.BigInteger;

import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.rlp.RLP;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.constants.SerializationTags;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.OracleQueryTransactionModel;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;

import io.reactivex.Single;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@ToString
public class OracleQueryTransaction extends AbstractTransaction<OracleQueryTransactionModel> {

	@NonNull
	private ExternalApi externalApi;

	@Override
	protected Single<UnsignedTx> createInternal() {
		return this.externalApi.rxPostOracleQuery(model.toApiModel());
	}

	@Override
	protected Bytes createRLPEncodedList() {
		Bytes encodedRlp = RLP.encodeList(rlpWriter -> {
			rlpWriter.writeInt(SerializationTags.OBJECT_TAG_ORACLE_QUERY_TRANSACTION);
			rlpWriter.writeInt(SerializationTags.VSN);
			byte[] senderIdWithTag = EncodingUtils.decodeCheckAndTag(model.getSenderId(),
					SerializationTags.ID_TAG_ACCOUNT);
			rlpWriter.writeByteArray(senderIdWithTag);
			this.checkZeroAndWriteValue(rlpWriter, model.getNonce());
			byte[] oracleIdWithTag = EncodingUtils.decodeCheckAndTag(model.getOracleId(),
					SerializationTags.ID_TAG_ORACLE);
			rlpWriter.writeByteArray(oracleIdWithTag);
			rlpWriter.writeByteArray(model.getQuery().getBytes());
			this.checkZeroAndWriteValue(rlpWriter, model.getQueryFee());
			switch (model.getQueryTtl().getType()) {
			case DELTA:
				this.checkZeroAndWriteValue(rlpWriter, BigInteger.ZERO);
				break;
			case BLOCK:
				this.checkZeroAndWriteValue(rlpWriter, BigInteger.ONE);
			}
			this.checkZeroAndWriteValue(rlpWriter, model.getQueryTtl().getValue());
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
