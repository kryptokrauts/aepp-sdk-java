package com.kryptokrauts.aeternity.sdk.wallet.service;

import com.kryptokrauts.aeternity.sdk.domain.secret.impl.RawKeyPair;
import com.kryptokrauts.aeternity.sdk.exception.AException;

public interface WalletService
{

    String generateWalletFile( RawKeyPair rawKeyPair, String walletPassword, String walletName ) throws AException;

    byte[] recoverPrivateKeyFromWalletFile( String json, String walletPassword ) throws AException;

    String getWalletAddress( RawKeyPair rawKeyPair );
}
