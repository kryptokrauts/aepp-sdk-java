package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import java.math.BigInteger;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.NameClaimTx;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.NameClaimTransaction;
import com.kryptokrauts.aeternity.sdk.util.ValidationUtil;
import com.kryptokrauts.sophia.compiler.generated.api.rxjava.DefaultApi;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class NameClaimTransactionModel extends AbstractTransactionModel<NameClaimTx> {

	@NonNull
	private String accountId;
	@NonNull
	private BigInteger nonce;
	@NonNull
	private String name;
	@NonNull
	private BigInteger nameSalt;
	@NonNull
	private BigInteger ttl;

	@Override
	public NameClaimTx toApiModel() {
		NameClaimTx nameClaimTx = new NameClaimTx();
		nameClaimTx.setAccountId(this.accountId);
		nameClaimTx.setNonce(this.nonce);
		nameClaimTx.setName(this.name);
		nameClaimTx.setNameSalt(this.nameSalt);
		nameClaimTx.setFee(this.fee);
		nameClaimTx.setTtl(this.ttl);
		return nameClaimTx;
	}

	@Override
	public void validateInput() {
		ValidationUtil.checkNamespace(this.name);
	}

	@Override
	public AbstractTransaction<?> buildTransaction(ExternalApi externalApi, DefaultApi compilerApi) {
		return NameClaimTransaction.builder().externalApi(externalApi).model(this).build();
	}

}
