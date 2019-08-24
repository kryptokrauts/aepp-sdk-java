package com.kryptokrauts.aeternity.sdk.service.transaction.type.impl;

import java.math.BigInteger;

import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.rlp.RLP;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.constants.SerializationTags;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.OracleExtendTransactionModel;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;

import io.reactivex.Single;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@ToString
public class OracleExtendTransaction extends AbstractTransaction<OracleExtendTransactionModel> {

	@NonNull
	private ExternalApi externalApi;

	@Override
	protected Single<UnsignedTx> createInternal() {
		return this.externalApi.rxPostOracleExtend(model.toApiModel());
	}

	@Override
	protected Bytes createRLPEncodedList() {
		Bytes encodedRlp = RLP.encodeList(rlpWriter -> {
			rlpWriter.writeInt(SerializationTags.OBJECT_TAG_ORACLE_EXTEND_TRANSACTION);
			rlpWriter.writeInt(SerializationTags.VSN);
			byte[] oracleIdWithTag = EncodingUtils.decodeCheckAndTag(model.getOracleId(),
					SerializationTags.ID_TAG_ORACLE);
			rlpWriter.writeByteArray(oracleIdWithTag);
			this.checkZeroAndWriteValue(rlpWriter, model.getNonce());
			switch (model.getOracleRelativeTtl().getType()) {
			case DELTA:
				this.checkZeroAndWriteValue(rlpWriter, BigInteger.ZERO);
				break;
			}
			this.checkZeroAndWriteValue(rlpWriter, model.getOracleRelativeTtl().getValue());
			this.checkZeroAndWriteValue(rlpWriter, model.getFee());
			this.checkZeroAndWriteValue(rlpWriter, model.getTtl());
		});
		return encodedRlp;
	}
}
