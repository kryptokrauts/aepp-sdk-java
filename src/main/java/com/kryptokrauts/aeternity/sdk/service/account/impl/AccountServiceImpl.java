package com.kryptokrauts.aeternity.sdk.service.account.impl;

import java.util.Optional;

import javax.annotation.Nonnull;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.sdk.service.ServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.account.AccountService;
import com.kryptokrauts.aeternity.sdk.service.domain.account.AccountResult;

import io.reactivex.Single;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class AccountServiceImpl implements AccountService {

	@Nonnull
	private ServiceConfiguration config;

	@Nonnull
	private ExternalApi externalApi;

	@Override
	public AccountResult blockingGetAccount(final Optional<String> base58PublicKey) {
		return AccountResult.builder().build().blockingGet(
				externalApi.rxGetAccountByPubkey(base58PublicKey.orElse(config.getBaseKeyPair().getPublicKey())));
	}

	@Override
	public Single<AccountResult> asyncGetAccount(final Optional<String> base58PublicKey) {
		return AccountResult.builder().build().asyncGet(
				externalApi.rxGetAccountByPubkey(base58PublicKey.orElse(config.getBaseKeyPair().getPublicKey())));
	}
}
