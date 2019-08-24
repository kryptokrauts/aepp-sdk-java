package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.NamePointer;
import com.kryptokrauts.aeternity.generated.model.NameUpdateTx;
import com.kryptokrauts.aeternity.sdk.constants.ApiIdentifiers;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.NameUpdateTransaction;
import com.kryptokrauts.aeternity.sdk.util.ValidationUtil;
import com.kryptokrauts.sophia.compiler.generated.api.rxjava.DefaultApi;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class NameUpdateTransactionModel extends AbstractTransactionModel<NameUpdateTx> {

	@NonNull
	private String accountId;
	@NonNull
	private BigInteger nonce;
	@NonNull
	private String nameId;
	@NonNull
	private BigInteger ttl;
	@NonNull
	private BigInteger nameTtl;
	@NonNull
	private BigInteger clientTtl;
	@NonNull
	private List<NamePointer> pointers;

	@Override
	public NameUpdateTx toApiModel() {
		NameUpdateTx nameUpdateTx = new NameUpdateTx();
		nameUpdateTx.setAccountId(this.accountId);
		nameUpdateTx.setNonce(this.nonce);
		nameUpdateTx.setNameId(nameId);
		nameUpdateTx.setFee(this.fee);
		nameUpdateTx.setTtl(this.ttl);
		nameUpdateTx.setNameTtl(nameTtl);
		nameUpdateTx.setClientTtl(clientTtl);
		nameUpdateTx.setPointers(pointers);
		return nameUpdateTx;
	}

	@Override
	public void validateInput() {
		// Validate parameters
		ValidationUtil.checkParameters(validate -> Optional.ofNullable(nameId != null), nameId,
				"validateUpdateTransaction", Arrays.asList("nameId"), ValidationUtil.PARAMETER_IS_NULL);
		ValidationUtil.checkParameters(validate -> Optional.ofNullable(nameId.startsWith(ApiIdentifiers.NAME)), nameId,
				"validateUpdateTransaction", Arrays.asList("nameId", ApiIdentifiers.NAME),
				ValidationUtil.MISSING_API_IDENTIFIER);
	}

	@Override
	public AbstractTransaction<?> buildTransaction(ExternalApi externalApi, DefaultApi compilerApi) {
		return NameUpdateTransaction.builder().externalApi(externalApi).model(this).build();
	}

}
