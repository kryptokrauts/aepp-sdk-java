package com.kryptokrauts.aeternity.sdk.domain.secret;

/**
 * a representation of private and public key pair
 *
 * @param <T> the type of a KeyPair
 */
public interface KeyPair<T> {

  T getPublicKey();

  T getPrivateKey();
}
