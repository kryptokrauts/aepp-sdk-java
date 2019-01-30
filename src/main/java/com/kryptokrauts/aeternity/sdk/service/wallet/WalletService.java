package com.kryptokrauts.aeternity.sdk.service.wallet;

import com.kryptokrauts.aeternity.sdk.domain.secret.impl.RawKeyPair;
import com.kryptokrauts.aeternity.sdk.exception.AException;

public interface WalletService {

    String generateWalletFile( RawKeyPair rawKeyPair, String walletPassword, String walletName ) throws AException;

    byte[] recoverPrivateKeyFromWalletFile( String json, String walletPassword ) throws AException;

    String getPublicKey( RawKeyPair rawKeyPair );
}
