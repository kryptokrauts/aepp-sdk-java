package com.kryptokrauts.aeternity.sdk.service.transaction.type.impl;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;

import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.rlp.RLP;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.CreateContractUnsignedTx;
import com.kryptokrauts.aeternity.sdk.constants.SerializationTags;
import com.kryptokrauts.aeternity.sdk.service.transaction.fee.FeeCalculationModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.fee.impl.ContractCreateFeeCalculationModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.ContractCreateTransactionModel;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;

import io.reactivex.Single;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@ToString
public class ContractCreateTransaction extends AbstractTransaction<ContractCreateTransactionModel> {

	@NonNull
	private ExternalApi externalApi;

	@Override
	protected Single<CreateContractUnsignedTx> createInternal() {
		return externalApi.rxPostContractCreate(model.toApiModel());
	}

	@Override
	protected Bytes createRLPEncodedList() {
		Bytes encodedRlp = RLP.encodeList(rlpWriter -> {
			rlpWriter.writeInt(SerializationTags.OBJECT_TAG_CONTRACT_CREATE_TRANSACTION);
			rlpWriter.writeInt(SerializationTags.VSN);
			byte[] ownerWithTag = EncodingUtils.decodeCheckAndTag(model.getOwnerId(), SerializationTags.ID_TAG_ACCOUNT);
			rlpWriter.writeByteArray(ownerWithTag);
			this.checkZeroAndWriteValue(rlpWriter, model.getNonce());
			rlpWriter.writeByteArray(EncodingUtils.decodeCheckWithIdentifier(model.getContractByteCode()));
			this.checkZeroAndWriteValue(rlpWriter, this.calculateVersion());
			this.checkZeroAndWriteValue(rlpWriter, model.getFee());
			this.checkZeroAndWriteValue(rlpWriter, model.getTtl());
			this.checkZeroAndWriteValue(rlpWriter, model.getDeposit());
			this.checkZeroAndWriteValue(rlpWriter, model.getAmount());
			this.checkZeroAndWriteValue(rlpWriter, model.getGas());
			this.checkZeroAndWriteValue(rlpWriter, model.getGasPrice());
			rlpWriter.writeByteArray(EncodingUtils.decodeCheckWithIdentifier(model.getCallData()));
		});
		return encodedRlp;
	}

	private BigInteger calculateVersion() {
		try {
			ByteBuffer vm = ByteBuffer.allocate(8);
			vm.putInt(model.getVmVersion().intValue());
			vm.putInt(model.getAbiVersion().intValue());

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			outputStream.write(vm.array());

			StringBuffer versionBuffer = new StringBuffer();

			for (byte b : outputStream.toByteArray()) {
				versionBuffer.append(String.format("%x", b));
			}
			return BigInteger.valueOf(Integer.parseInt(versionBuffer.toString(), 16));
		} catch (Exception e) {
			System.err.println(
					String.format("Error occured calculating version from parameters vmVersion %s and abiVersion %s",
							model.getVmVersion(), model.getAbiVersion()));
			return null;
		}
	}

	@Override
	protected FeeCalculationModel getFeeCalculationModel() {
		return new ContractCreateFeeCalculationModel();
	}
}
