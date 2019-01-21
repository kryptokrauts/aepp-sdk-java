package com.kryptokrauts.aeternity.sdk.keypair.service;

import com.kryptokrauts.aeternity.sdk.domain.secret.impl.BaseKeyPair;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.RawKeyPair;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface KeyPairService {

    /**
     * generate a securely randomed salt of given size
     *
     * @param size
     * @return
     */
    byte[] generateSalt(int size);

    /**
     * @return a base58 encoded keypair
     */
    BaseKeyPair generateBaseKeyPair();

    /**
     * @return a raw keypair
     */
    RawKeyPair generateRawKeyPair();

    /**
     *
     * @param privateKey
     * @return a base58 encoded keypair
     */
    BaseKeyPair generateBaseKeyPairFromSecret(String privateKey);

    /**
     *
     * @param privateKey
     * @return a raw keypair
     */
    RawKeyPair generateRawKeyPairFromSecret(String privateKey);

    byte[] encryptPrivateKey(String password, byte[] binaryKey) throws NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException;

    byte[] encryptPublicKey(String password, byte[] binaryKey) throws NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException;

    byte[] decryptPrivateKey(String password, byte[] encryptedBinaryKey) throws NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException;

    byte[] decryptPublicKey(String password, byte[] encryptedBinaryKey) throws NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException;

    RawKeyPair encryptRawKeyPair(RawKeyPair keyPairRaw, String password) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException;
}
