package com.kryptokrauts.aeternity.sdk.service.domain.account;

import java.math.BigInteger;

import com.kryptokrauts.aeternity.generated.model.Account;
import com.kryptokrauts.aeternity.sdk.service.domain.GenericServiceResultObject;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@ToString
public class AccountResult extends GenericServiceResultObject<Account, AccountResult> {

	private String publicKey;

	private BigInteger balance;

	private BigInteger nonce;

	private String kind;

	private String gaContractId;

	private String gaAuthenticationFunction;

	@Override
	protected AccountResult map(Account generatedResultObject) {
		if (generatedResultObject != null)
			return AccountResult.builder().publicKey(generatedResultObject.getId())
					.balance(generatedResultObject.getBalance()).nonce(generatedResultObject.getNonce())
					.kind(generatedResultObject.getKind().toString())
					.gaContractId(generatedResultObject.getContractId())
					.gaAuthenticationFunction(generatedResultObject.getAuthFun()).build();
		else
			return AccountResult.builder().build();
	}

	@Override
	protected String getResultObjectClassName() {
		return AccountResult.class.getName();
	}
}
