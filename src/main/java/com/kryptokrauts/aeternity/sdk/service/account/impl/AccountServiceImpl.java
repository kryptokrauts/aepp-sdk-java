package com.kryptokrauts.aeternity.sdk.service.account.impl;

import javax.annotation.Nonnull;

import com.kryptokrauts.aeternity.generated.api.AccountApiImpl;
import com.kryptokrauts.aeternity.generated.api.rxjava.AccountApi;
import com.kryptokrauts.aeternity.sdk.service.ServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.account.AccountService;
import com.kryptokrauts.aeternity.sdk.service.account.domain.AccountResult;

import io.reactivex.Single;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class AccountServiceImpl implements AccountService {

	@Nonnull
	private ServiceConfiguration config;

	private AccountApi accountApi;

	private AccountApi getAccountApi() {
		if (accountApi == null) {
			accountApi = new AccountApi(new AccountApiImpl(config.getApiClient()));
		}
		return accountApi;
	}

	@Override
	public AccountResult blockingGetAccount(final String base58PublicKey) {
		return AccountResult.builder().build().blockingGet(getAccountApi().rxGetAccountByPubkey(base58PublicKey));
	}

	@Override
	public Single<AccountResult> asyncGetAccount(final String base58PublicKey) {
		return AccountResult.builder().build().asyncGet(getAccountApi().rxGetAccountByPubkey(base58PublicKey));
	}
}
