package com.kryptokrauts.aeternity.sdk.signer.service;

import org.bouncycastle.crypto.CryptoException;

public interface SignerService {

    byte[] sign(String data, String privateKey) throws CryptoException;

    byte[] sign(byte[] data, String privateKey) throws CryptoException;

    byte[] signPersonalMessage(String message, String privateKey) throws CryptoException;

    boolean verify(String data, byte[] signature, String publicKey);

    boolean verify(byte[] data, byte[] signature, String publicKey);

    boolean verifyPersonalMessage(String message, byte[] signature, String publicKey);

}
