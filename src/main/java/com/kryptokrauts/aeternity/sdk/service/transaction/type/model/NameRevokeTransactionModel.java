package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Optional;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.NameRevokeTx;
import com.kryptokrauts.aeternity.sdk.constants.ApiIdentifiers;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.NameRevokeTransaction;
import com.kryptokrauts.aeternity.sdk.util.ValidationUtil;
import com.kryptokrauts.sophia.compiler.generated.api.rxjava.DefaultApi;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class NameRevokeTransactionModel extends AbstractTransactionModel<NameRevokeTx> {

	@NonNull
	private String accountId;
	@NonNull
	private BigInteger nonce;
	@NonNull
	private String nameId;
	@NonNull
	private BigInteger ttl;

	@Override
	public NameRevokeTx toApiModel() {
		NameRevokeTx nameRevokeTx = new NameRevokeTx();
		nameRevokeTx.setAccountId(this.accountId);
		nameRevokeTx.setNonce(this.nonce);
		nameRevokeTx.setNameId(nameId);
		nameRevokeTx.setFee(this.fee);
		nameRevokeTx.setTtl(this.ttl);
		return nameRevokeTx;
	}

	@Override
	public void validateInput() {
		// Validate parameters
		ValidationUtil.checkParameters(validate -> Optional.ofNullable(nameId != null), nameId,
				"validateRevokeTransaction", Arrays.asList("nameId"), ValidationUtil.PARAMETER_IS_NULL);
		ValidationUtil.checkParameters(validate -> Optional.ofNullable(nameId.startsWith(ApiIdentifiers.NAME)), nameId,
				"validateRevokeTransaction", Arrays.asList("nameId", ApiIdentifiers.NAME),
				ValidationUtil.MISSING_API_IDENTIFIER);
	}

	@Override
	public AbstractTransaction<?> buildTransaction(ExternalApi externalApi, DefaultApi compilerApi) {
		return NameRevokeTransaction.builder().externalApi(externalApi).model(this).build();
	}

}
