package com.kryptokrauts.aeternity.sdk.service.keypair;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.kryptokrauts.aeternity.sdk.domain.secret.impl.BaseKeyPair;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.RawKeyPair;

public interface KeyPairService {

    /**
     * @return a base58 encoded keypair
     */
    BaseKeyPair generateBaseKeyPair();

    /**
     * 
     * @return a byte arrayed keypair
     */
    RawKeyPair generateRawKeyPair();

    /**
     *
     * @param privateKey
     * @return a base58 encoded keypair
     */
    BaseKeyPair generateBaseKeyPairFromSecret( String privateKey );

    /**
     *
     * @param privateKey
     * @return a raw keypair
     */
    RawKeyPair generateRawKeyPairFromSecret( String privateKey );

    byte[] encryptPrivateKey( String password, byte[] binaryKey )
    throws NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException;

    byte[] encryptPublicKey( String password, byte[] binaryKey )
    throws NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException;

    byte[] decryptPrivateKey( String password, byte[] encryptedBinaryKey )
    throws NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException;

    byte[] decryptPublicKey( String password, byte[] encryptedBinaryKey )
    throws NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException;

    RawKeyPair encryptRawKeyPair( RawKeyPair keyPairRaw, String password )
    throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException;
}
