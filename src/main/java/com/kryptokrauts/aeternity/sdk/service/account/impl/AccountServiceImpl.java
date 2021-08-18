package com.kryptokrauts.aeternity.sdk.service.account.impl;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.sdk.service.ServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.account.AccountService;
import com.kryptokrauts.aeternity.sdk.service.account.domain.AccountResult;
import com.kryptokrauts.aeternity.sdk.service.account.domain.NextNonceStrategy;
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
        .asyncGet(externalApi.rxGetAccountByPubkey(config.getKeyPair().getAddress(), false));
  }

  @Override
  public Single<AccountResult> asyncGetAccount(final String base58PublicKey) {
    return AccountResult.builder()
        .build()
        .asyncGet(externalApi.rxGetAccountByPubkey(base58PublicKey, false));
  }

  @Override
  public AccountResult blockingGetAccount() {
    return AccountResult.builder()
        .build()
        .blockingGet(externalApi.rxGetAccountByPubkey(config.getKeyPair().getAddress(), false));
  }

  @Override
  public AccountResult blockingGetAccount(final String base58PublicKey) {
    return AccountResult.builder()
        .build()
        .blockingGet(externalApi.rxGetAccountByPubkey(base58PublicKey, false));
  }

  @Override
  public Single<BigInteger> asyncGetNextNonce() {
    return externalApi
        .rxGetAccountNextNonce(
            config.getKeyPair().getAddress(), false, NextNonceStrategy.MAX.toString())
        .map(res -> res.getNextNonce());
  }

  @Override
  public Single<BigInteger> asyncGetNextNonce(final NextNonceStrategy nextNonceStrategy) {
    return externalApi
        .rxGetAccountNextNonce(
            config.getKeyPair().getAddress(), false, nextNonceStrategy.toString())
        .map(res -> res.getNextNonce());
  }

  @Override
  public Single<BigInteger> asyncGetNextNonce(final String base58PublicKey) {
    return externalApi
        .rxGetAccountNextNonce(base58PublicKey, false, NextNonceStrategy.MAX.toString())
        .map(res -> res.getNextNonce());
  }

  @Override
  public Single<BigInteger> asyncGetNextNonce(
      final String base58PublicKey, final NextNonceStrategy nextNonceStrategy) {
    return externalApi
        .rxGetAccountNextNonce(base58PublicKey, false, nextNonceStrategy.toString())
        .map(res -> res.getNextNonce());
  }

  @Override
  public BigInteger blockingGetNextNonce() {
    return externalApi
        .rxGetAccountNextNonce(
            config.getKeyPair().getAddress(), false, NextNonceStrategy.MAX.toString())
        .blockingGet()
        .getNextNonce();
  }

  @Override
  public BigInteger blockingGetNextNonce(final NextNonceStrategy nextNonceStrategy) {
    return externalApi
        .rxGetAccountNextNonce(
            config.getKeyPair().getAddress(), false, nextNonceStrategy.toString())
        .blockingGet()
        .getNextNonce();
  }

  @Override
  public BigInteger blockingGetNextNonce(final String base58PublicKey) {
    return externalApi
        .rxGetAccountNextNonce(base58PublicKey, false, NextNonceStrategy.MAX.toString())
        .blockingGet()
        .getNextNonce();
  }

  @Override
  public BigInteger blockingGetNextNonce(
      final String base58PublicKey, final NextNonceStrategy nextNonceStrategy) {
    return externalApi
        .rxGetAccountNextNonce(base58PublicKey, false, nextNonceStrategy.toString())
        .blockingGet()
        .getNextNonce();
  }
}
