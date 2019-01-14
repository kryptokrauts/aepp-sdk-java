package com.kryptokrauts.aeternity.sdk.domain.secret;

public interface KeyPair<T> {

    T getPublicKey();

    T getPrivateKey();
}
