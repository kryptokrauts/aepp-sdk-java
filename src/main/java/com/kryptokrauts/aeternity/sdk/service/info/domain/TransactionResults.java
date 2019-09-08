package com.kryptokrauts.aeternity.sdk.service.info.domain;

import java.util.List;
import java.util.stream.Collectors;

import com.kryptokrauts.aeternity.generated.model.GenericTxs;
import com.kryptokrauts.aeternity.sdk.domain.GenericResultObject;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@ToString
public class TransactionResults extends GenericResultObject<GenericTxs, TransactionResults> {

	private List<TransactionResult> results;

	@Override
	protected TransactionResults map(GenericTxs generatedResultObject) {
		if (generatedResultObject != null)
			return this.toBuilder()
					.results(generatedResultObject.getTransactions().stream()
							.map(tx -> TransactionResult.builder().build().map(tx)).collect(Collectors.toList()))
					.build();
		else
			return this.toBuilder().build();
	}

	@Override
	protected String getResultObjectClassName() {
		return this.getClass().getName();
	}

}
