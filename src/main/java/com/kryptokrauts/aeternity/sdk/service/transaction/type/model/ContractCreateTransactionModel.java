package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import java.math.BigInteger;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.ContractCreateTx;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.ContractCreateTransaction;
import com.kryptokrauts.sophia.compiler.generated.api.rxjava.DefaultApi;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ContractCreateTransactionModel extends AbstractTransactionModel<ContractCreateTx> {

	@NonNull
	private BigInteger abiVersion;
	@NonNull
	private BigInteger amount;
	@NonNull
	private String callData;
	@NonNull
	private String contractByteCode;
	@NonNull
	private BigInteger deposit;
	@NonNull
	private BigInteger gas;
	@NonNull
	private BigInteger gasPrice;
	@NonNull
	private BigInteger nonce;
	@NonNull
	private String ownerId;
	@NonNull
	private BigInteger ttl;
	@NonNull
	private BigInteger vmVersion;

	@Override
	public ContractCreateTx toApiModel() {
		ContractCreateTx contractCreateTx = new ContractCreateTx();
		contractCreateTx.setAbiVersion(abiVersion);
		contractCreateTx.setAmount(amount);
		contractCreateTx.setCallData(callData);
		contractCreateTx.setCode(contractByteCode);
		contractCreateTx.setDeposit(deposit);
		contractCreateTx.setFee(fee);
		contractCreateTx.setGas(gas);
		contractCreateTx.setGasPrice(gasPrice);
		contractCreateTx.setNonce(nonce);
		contractCreateTx.setOwnerId(ownerId);
		contractCreateTx.setTtl(ttl);
		contractCreateTx.setVmVersion(vmVersion);

		return contractCreateTx;
	}

	@Override
	public void validateInput() {
		// TODO Auto-generated method stub

	}

	@Override
	public AbstractTransaction<?> buildTransaction(ExternalApi externalApi, DefaultApi compilerApi) {
		return ContractCreateTransaction.builder().externalApi(externalApi).compilerApi(compilerApi).model(this)
				.build();
	}

}
