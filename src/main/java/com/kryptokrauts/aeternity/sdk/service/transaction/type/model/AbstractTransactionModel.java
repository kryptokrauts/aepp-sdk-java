package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import java.math.BigInteger;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.sophia.compiler.generated.api.rxjava.DefaultApi;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public abstract class AbstractTransactionModel<GeneratedTxModel> {

	/**
	 * the fee is automatically calculated but can also be set manually
	 */
	protected BigInteger fee;

	/**
	 * this method needs to be implemented for testing purposes (non native mode)
	 * and returns the generated tx model from the transaction fields
	 *
	 * @return one of {@link com.kryptokrauts.aeternity.generated.model}
	 */
	public abstract GeneratedTxModel toApiModel();

	/**
	 * this method can be used to perform transaction specific validations that will
	 */
	public abstract void validateInput();

	public abstract AbstractTransaction<?> buildTransaction(ExternalApi externalApi, DefaultApi compilerApi);
}
