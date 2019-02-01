package com.kryptokrauts.aeternity.sdk.service.account;

import com.kryptokrauts.aeternity.generated.epoch.model.Account;
import com.kryptokrauts.aeternity.sdk.util.EncodingType;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;

import io.reactivex.Observable;

public interface AccountService {

    /**
     * the publicKey encoded using Base58 encoding, see {@link EncodingType},
     * {@link EncodingUtils}
     * 
     * @param publicKey
     * @return the account wrapped in an observable
     */
    Observable<Account> getAccount( String publicKey );

}
