package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import java.math.BigInteger;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.NamePreclaimTx;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.NamePreclaimTransaction;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import com.kryptokrauts.aeternity.sdk.util.ValidationUtil;
import com.kryptokrauts.sophia.compiler.generated.api.rxjava.DefaultApi;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class NamePreclaimTransactionModel extends AbstractTransactionModel<NamePreclaimTx> {

	@NonNull
	private String accountId;
	@NonNull
	private String name; // will be used to generate the commitmentId
	@NonNull
	private BigInteger salt; // will be used to generate the commitmentId
	@NonNull
	private BigInteger nonce;
	@NonNull
	private BigInteger ttl;

	@Override
	public NamePreclaimTx toApiModel() {
		NamePreclaimTx namePreclaimTx = new NamePreclaimTx();
		namePreclaimTx.setAccountId(this.accountId);
		namePreclaimTx.setCommitmentId(EncodingUtils.generateCommitmentHash(this.name, this.salt));
		namePreclaimTx.setFee(this.fee);
		namePreclaimTx.setNonce(this.nonce);
		namePreclaimTx.setTtl(this.ttl);
		return namePreclaimTx;
	}

	@Override
	public void validateInput() {
		ValidationUtil.checkNamespace(this.name);
	}

	@Override
	public AbstractTransaction<?> buildTransaction(ExternalApi externalApi, DefaultApi compilerApi) {
		return NamePreclaimTransaction.builder().externalApi(externalApi).model(this).build();
	}

}
