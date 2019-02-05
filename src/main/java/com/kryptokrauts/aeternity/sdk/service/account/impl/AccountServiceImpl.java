package com.kryptokrauts.aeternity.sdk.service.account.impl;

import com.kryptokrauts.aeternity.generated.api.AccountApiImpl;
import com.kryptokrauts.aeternity.generated.api.rxjava.AccountApi;
import com.kryptokrauts.aeternity.generated.model.Account;
import com.kryptokrauts.aeternity.sdk.service.ServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.account.AccountService;
import io.reactivex.Single;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnull;

@RequiredArgsConstructor
public final class AccountServiceImpl implements AccountService {

    @Nonnull
    private ServiceConfiguration config;

    private AccountApi accountApi;

    private AccountApi getAccountApi() {
        if ( accountApi == null ) {
            accountApi = new AccountApi( new AccountApiImpl( config.getApiClient() ) );
        }
        return accountApi;
    }

    @Override
    public Single<Account> getAccount(final String base58PublicKey ) {
        return getAccountApi().rxGetAccountByPubkey( base58PublicKey );
    }

}
