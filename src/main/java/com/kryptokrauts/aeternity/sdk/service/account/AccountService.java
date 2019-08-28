package com.kryptokrauts.aeternity.sdk.service.account;

import com.kryptokrauts.aeternity.sdk.service.domain.account.AccountResult;
import com.kryptokrauts.aeternity.sdk.util.EncodingType;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import io.reactivex.Single;
import java.util.Optional;

public interface AccountService {

  /**
   * the publicKey encoded
   *
   * <p>import io.reactivex.Single; using Base58 encoding, see {@link EncodingType}, {@link
   * EncodingUtils}
   *
   * @param publicKey
   * @return the account wrapped in a single
   */
  AccountResult blockingGetAccount(Optional<String> publicKey);

  Single<AccountResult> asyncGetAccount(Optional<String> base58PublicKey);
}
