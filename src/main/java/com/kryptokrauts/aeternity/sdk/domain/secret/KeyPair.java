package com.kryptokrauts.aeternity.sdk.domain.secret;

/**
 * a representation of private and public key pair
 * 
 * @param <T>
 */
public interface KeyPair<T> {

    T getPublicKey();

    T getPrivateKey();
}
