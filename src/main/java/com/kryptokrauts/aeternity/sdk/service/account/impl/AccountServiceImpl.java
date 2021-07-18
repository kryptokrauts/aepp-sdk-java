package com.kryptokrauts.aeternity.sdk.service.account.impl;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.sdk.service.ServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.account.AccountService;
import com.kryptokrauts.aeternity.sdk.service.account.domain.AccountResult;
import io.reactivex.Single;
import java.math.BigInteger;
import javax.annotation.Nonnull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class AccountServiceImpl implements AccountService {

  @Nonnull private ServiceConfiguration config;

  @Nonnull private ExternalApi externalApi;

  @Override
  public Single<AccountResult> asyncGetAccount() {
    return AccountResult.builder()
        .build()
        .asyncGet(externalApi.rxGetAccountByPubkey(config.getKeyPair().getAddress(), false, null));
  }

  @Override
  public Single<AccountResult> asyncGetAccount(final String base58PublicKey) {
    return AccountResult.builder()
        .build()
        .asyncGet(externalApi.rxGetAccountByPubkey(base58PublicKey, false, null));
  }

  @Override
  public AccountResult blockingGetAccount() {
    return AccountResult.builder()
        .build()
        .blockingGet(
            externalApi.rxGetAccountByPubkey(config.getKeyPair().getAddress(), false, null));
  }

  @Override
  public AccountResult blockingGetAccount(final String base58PublicKey) {
    return AccountResult.builder()
        .build()
        .blockingGet(externalApi.rxGetAccountByPubkey(base58PublicKey, false, null));
  }

  @Override
  public Single<BigInteger> asyncGetNextBaseKeypairNonce() {
    return this.asyncGetAccount().map(ar -> ar.getNonce().add(BigInteger.ONE));
  }

  @Override
  public Single<BigInteger> asyncGetNextBaseKeypairNonce(final String base58PublicKey) {
    return this.asyncGetAccount(base58PublicKey).map(ar -> ar.getNonce().add(BigInteger.ONE));
  }

  @Override
  public BigInteger blockingGetNextBaseKeypairNonce() {
    return this.blockingGetAccount().getNonce().add(BigInteger.ONE);
  }

  @Override
  public BigInteger blockingGetNextBaseKeypairNonce(final String base58PublicKey) {
    return this.blockingGetAccount(base58PublicKey).getNonce().add(BigInteger.ONE);
  }
}
