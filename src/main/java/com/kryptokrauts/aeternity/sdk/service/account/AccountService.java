package com.kryptokrauts.aeternity.sdk.service.account;

import com.kryptokrauts.aeternity.generated.model.Account;
import com.kryptokrauts.aeternity.sdk.util.EncodingType;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import io.reactivex.Single;

public interface AccountService {

  /**
   * the publicKey encoded using Base58 encoding, see {@link EncodingType}, {@link EncodingUtils}
   *
   * @param publicKey
   * @return the account wrapped in a single
   */
  Single<Account> getAccount(String publicKey);
}
