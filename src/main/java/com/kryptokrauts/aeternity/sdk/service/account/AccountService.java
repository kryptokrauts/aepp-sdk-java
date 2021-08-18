package com.kryptokrauts.aeternity.sdk.service.account;

import com.kryptokrauts.aeternity.sdk.service.account.domain.AccountResult;
import com.kryptokrauts.aeternity.sdk.service.account.domain.NextNonceStrategy;
import com.kryptokrauts.aeternity.sdk.util.EncodingType;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import io.reactivex.Single;
import java.math.BigInteger;

public interface AccountService {

  /**
   * synchronously get the account
   *
   * <p>using the account for the KeyPair defined in the {@link
   * com.kryptokrauts.aeternity.sdk.service.ServiceConfiguration}
   *
   * @return the account wrapped in a single {@link AccountResult}
   */
  AccountResult blockingGetAccount();

  /**
   * synchronously get the account
   *
   * <p>using Base58 encoding, see {@link EncodingType}, {@link EncodingUtils}
   *
   * @param base58PublicKey the encoded publicKey (ak_...)
   * @return the account wrapped in a single {@link AccountResult}
   */
  AccountResult blockingGetAccount(String base58PublicKey);

  /**
   * asynchronously get the account
   *
   * <p>using the account for the KeyPair defined in the {@link
   * com.kryptokrauts.aeternity.sdk.service.ServiceConfiguration}
   *
   * @return asynchronous result handler (RxJava Single) for {@link AccountResult}
   */
  Single<AccountResult> asyncGetAccount();

  /**
   * asynchronously get the account
   *
   * <p>import io.reactivex.Single; using Base58 encoding, see {@link EncodingType}, {@link
   * EncodingUtils}
   *
   * @param base58PublicKey the encoded publicKey (ak_...)
   * @return asynchronous result handler (RxJava Single) for {@link AccountResult}
   */
  Single<AccountResult> asyncGetAccount(String base58PublicKey);

  /**
   * asynchronously given accounts next nonce using "max" strategy
   *
   * <p>using the account for the KeyPair defined in the {@link
   * com.kryptokrauts.aeternity.sdk.service.ServiceConfiguration}
   *
   * @return next nonce
   */
  Single<BigInteger> asyncGetNextNonce();

  /**
   * asynchronously given accounts next nonce
   *
   * <p>using the account for the KeyPair defined in the {@link
   * com.kryptokrauts.aeternity.sdk.service.ServiceConfiguration}
   *
   * @param nextNonceStrategy the strategy to use, one of: max, continuity
   * @return next nonce
   */
  Single<BigInteger> asyncGetNextNonce(NextNonceStrategy nextNonceStrategy);

  /**
   * asynchronously given accounts next nonce using "max" strategy
   *
   * <p>import io.reactivex.Single; using Base58 encoding, see {@link EncodingType}, {@link
   * EncodingUtils}
   *
   * @param base58PublicKey the encoded publicKey (ak_...)
   * @return next nonce
   */
  Single<BigInteger> asyncGetNextNonce(String base58PublicKey);

  /**
   * asynchronously given accounts next nonce
   *
   * <p>import io.reactivex.Single; using Base58 encoding, see {@link EncodingType}, {@link
   * EncodingUtils}
   *
   * @param base58PublicKey the encoded publicKey (ak_...)
   * @param nextNonceStrategy the strategy to use, one of: max, continuity
   * @return next nonce
   */
  Single<BigInteger> asyncGetNextNonce(String base58PublicKey, NextNonceStrategy nextNonceStrategy);

  /**
   * synchronously given accounts next nonce using "max" strategy
   *
   * <p>using the account for the KeyPair defined in the {@link
   * com.kryptokrauts.aeternity.sdk.service.ServiceConfiguration}
   *
   * @return next nonce
   */
  BigInteger blockingGetNextNonce();

  /**
   * synchronously given accounts next nonce
   *
   * <p>using the account for the KeyPair defined in the {@link
   * com.kryptokrauts.aeternity.sdk.service.ServiceConfiguration}
   *
   * @param nextNonceStrategy the strategy to use, one of: max, continuity
   * @return next nonce
   */
  BigInteger blockingGetNextNonce(NextNonceStrategy nextNonceStrategy);

  /**
   * synchronously given accounts next nonce using "max" strategy
   *
   * <p>import io.reactivex.Single; using Base58 encoding, see {@link EncodingType}, {@link
   * EncodingUtils}
   *
   * @param base58PublicKey the encoded publicKey (ak_...)
   * @return next nonce
   */
  BigInteger blockingGetNextNonce(String base58PublicKey);

  /**
   * synchronously given accounts next nonce
   *
   * <p>import io.reactivex.Single; using Base58 encoding, see {@link EncodingType}, {@link
   * EncodingUtils}
   *
   * @param base58PublicKey the encoded publicKey (ak_...)
   * @param nextNonceStrategy the strategy to use, one of: max, continuity
   * @return next nonce
   */
  BigInteger blockingGetNextNonce(String base58PublicKey, NextNonceStrategy nextNonceStrategy);
}
