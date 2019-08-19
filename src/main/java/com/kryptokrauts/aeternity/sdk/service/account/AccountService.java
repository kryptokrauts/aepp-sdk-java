package com.kryptokrauts.aeternity.sdk.service.account;

import java.util.Optional;

import com.kryptokrauts.aeternity.sdk.service.account.domain.AccountResult;
import com.kryptokrauts.aeternity.sdk.util.EncodingType;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;

import io.reactivex.Single;

public interface AccountService {

	/**
	 * the publicKey encoded
	 *
	 * <p>
	 * import io.reactivex.Single; using Base58 encoding, see {@link EncodingType},
	 * {@link EncodingUtils}
	 *
	 * @param publicKey
	 * @return the account wrapped in a single
	 */
	AccountResult blockingGetAccount(Optional<String> publicKey);

	Single<AccountResult> asyncGetAccount(Optional<String> base58PublicKey);
}
