package com.kryptokrauts.aeternity.sdk.wallet.service;

import com.kryptokrauts.aeternity.sdk.domain.secret.impl.BaseKeyPair;
import com.kryptokrauts.aeternity.sdk.exception.AException;

public interface WalletService {
    String generateWalletJSON(BaseKeyPair baseKeyPair, String password, String walletName) throws AException;
}
