package com.kryptokrauts.aeternity.sdk.service.transaction.type.impl;

import java.math.BigInteger;

import com.kryptokrauts.aeternity.generated.api.rxjava.ContractApi;
import com.kryptokrauts.aeternity.generated.model.ContractCreateTx;
import com.kryptokrauts.aeternity.generated.model.CreateContractUnsignedTx;
import com.kryptokrauts.aeternity.sdk.constants.SerializationTags;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;

import io.reactivex.Single;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import net.consensys.cava.bytes.Bytes;
import net.consensys.cava.rlp.RLP;

@Getter
@SuperBuilder
@ToString
public class CreateContractTransaction extends AbstractTransaction<ContractCreateTx> {

	@NonNull
	private Integer abiVersion;
	@NonNull
	private Integer amount;
	@NonNull
	private String callData;
	@NonNull
	private String contractByteCode;
	@NonNull
	private Integer deposit;
	@NonNull
	private Integer gas;
	@NonNull
	private Integer gasPrice;
	@NonNull
	private Integer nonce;
	@NonNull
	private String ownerId;
	@NonNull
	private Integer ttl;
	@NonNull
	private Integer vmVersion;

	@NonNull
	private ContractApi contractApi;

	@Override
	protected Single<CreateContractUnsignedTx> createInternal() {
		this.fee = new BigInteger(String.valueOf(Integer.MAX_VALUE));
		return contractApi.rxPostContractCreate(toModel());
	}

	@Override
	protected ContractCreateTx toModel() {
		ContractCreateTx contractCreateTx = new ContractCreateTx();
		contractCreateTx.setAbiVersion(abiVersion);
		contractCreateTx.setAmount(amount);
		contractCreateTx.setCallData(callData);
		contractCreateTx.setCode(contractByteCode);
		contractCreateTx.setDeposit(deposit);
		contractCreateTx.setFee(fee.intValue());
		contractCreateTx.setGas(gas);
		contractCreateTx.setGasPrice(gasPrice);
		contractCreateTx.setNonce(nonce);
		contractCreateTx.setOwnerId(ownerId);
		contractCreateTx.setTtl(ttl);
		contractCreateTx.setVmVersion(vmVersion);

		return contractCreateTx;
	}

	@Override
	protected Bytes createRLPEncodedList() {
		Bytes encodedRlp = RLP.encodeList(rlpWriter -> {
			rlpWriter.writeInt(SerializationTags.OBJECT_TAG_CONTRACT_CREATE_TRANSACTION);
			rlpWriter.writeInt(SerializationTags.VSN);
			byte[] ownerWithTag = EncodingUtils.decodeCheckAndTag(this.ownerId, SerializationTags.ID_TAG_ACCOUNT);
			rlpWriter.writeByteArray(ownerWithTag);

			rlpWriter.writeBigInteger(new BigInteger(this.nonce.toString()));
			rlpWriter.writeByteArray(EncodingUtils.decodeCheckWithIdentifier(contractByteCode));
			// rlpWriter.writeString(contractByteCode);
			rlpWriter.writeBigInteger(new BigInteger("196609"));
			rlpWriter.writeBigInteger(new BigInteger(String.valueOf(Integer.MAX_VALUE)));
			rlpWriter.writeBigInteger(new BigInteger(this.ttl.toString()));
			rlpWriter.writeBigInteger(BigInteger.ZERO);
			rlpWriter.writeBigInteger(BigInteger.ZERO);
			rlpWriter.writeBigInteger(new BigInteger("1000000"));
			rlpWriter.writeBigInteger(new BigInteger("1000000000"));
			rlpWriter.writeByteArray(EncodingUtils.decodeCheckWithIdentifier(callData));
			// rlpWriter.writeString(callData);
		});
		return encodedRlp;
	}

	private Bytes createContractRLP() {
		Bytes contractRLP = RLP.encodeList(rlpWriter -> {
		});

		return contractRLP;
	}
}
