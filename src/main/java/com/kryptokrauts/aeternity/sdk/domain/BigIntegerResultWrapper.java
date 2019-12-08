package com.kryptokrauts.aeternity.sdk.domain;

import java.math.BigInteger;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * this string result wrapper introduces the error handling when returning
 * simple string from node calls
 *
 * @author mitch
 */
@Getter
@SuperBuilder(toBuilder = true)
@ToString
public class BigIntegerResultWrapper
		extends
			GenericResultObject<BigInteger, BigIntegerResultWrapper> {

	private BigInteger result;

	@Override
	protected BigIntegerResultWrapper map(BigInteger generatedResultObject) {
		if (generatedResultObject != null)
			return this.toBuilder().result(generatedResultObject).build();
		else
			return this.toBuilder().build();
	}

	@Override
	protected String getResultObjectClassName() {
		return this.getClass().getName();
	}
}
