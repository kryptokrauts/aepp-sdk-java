package com.kryptokrauts.aeternity.sdk.service.account;

import com.kryptokrauts.aeternity.sdk.service.account.domain.AccountResult;
import com.kryptokrauts.aeternity.sdk.util.EncodingType;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import io.reactivex.Single;
import java.math.BigInteger;
import java.util.Optional;

public interface AccountService {

  /**
   * synchronously get the account
   *
   * <p>using Base58 encoding, see {@link EncodingType}, {@link EncodingUtils}
   *
   * @param the publicKey encoded
   * @return the account wrapped in a single {@link AccountResult}
   */
  AccountResult blockingGetAccount(Optional<String> publicKey);

  /**
   * asynchronously get the account
   *
   * <p>import io.reactivex.Single; using Base58 encoding, see {@link EncodingType}, {@link
   * EncodingUtils}
   *
   * @param the publicKey encoded
   * @return asynchronous result handler (RxJava Single) for {@link AccountResult}
   */
  Single<AccountResult> asyncGetAccount(Optional<String> base58PublicKey);

  /**
   * asynchronously given accounts next nonce
   *
   * <p>import io.reactivex.Single; using Base58 encoding, see {@link EncodingType}, {@link
   * EncodingUtils}
   *
   * @param the publicKey encoded
   * @return next nonce
   */
  Single<BigInteger> asyncGetNextBaseKeypairNonce(Optional<String> base58PublicKey);

  /**
   * synchronously given accounts next nonce
   *
   * <p>import io.reactivex.Single; using Base58 encoding, see {@link EncodingType}, {@link
   * EncodingUtils}
   *
   * @param the publicKey encoded
   * @return next nonce
   */
  BigInteger blockingGetNextBaseKeypairNonce(Optional<String> base58PublicKey);
}
