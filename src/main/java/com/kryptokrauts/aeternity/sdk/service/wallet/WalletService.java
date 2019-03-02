package com.kryptokrauts.aeternity.sdk.service.wallet;

import com.kryptokrauts.aeternity.sdk.domain.Keystore;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.MnemonicKeyPair;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.RawKeyPair;
import com.kryptokrauts.aeternity.sdk.exception.AException;

public interface WalletService {

  /**
   * generate a JSON keystore which can be stored in a file for later recovery of the private key
   *
   * @param rawKeyPair the public / private keypair
   * @param walletPassword the password for symmetric encryption
   * @param walletName the name of the keystore wallet
   * @return encrypts the keypair using the given walletPassword an returns a JSON derived from
   *     {@link Keystore}
   * @throws AException
   */
  String generateKeystore(RawKeyPair rawKeyPair, String walletPassword, String walletName)
      throws AException;

  /**
   * allows to recover a private key from a given keystore json
   *
   * @param json the keystore JSON derived from {@link Keystore}
   * @param walletPassword the symmetric password used to create the keystore
   * @return
   * @throws AException
   */
  byte[] recoverPrivateKeyFromKeystore(String json, String walletPassword) throws AException;

  /**
   * @param rawKeyPair
   * @return base58 encoded human readable publicKey
   */
  String getPublicKey(RawKeyPair rawKeyPair);

  /**
   * stores public key and mnemonic seed words (but not the password!) as JSON
   *
   * @param mnemonicKeyPair
   * @return the JSON keystore
   * @throws AException
   */
  String createHDKeystore(MnemonicKeyPair mnemonicKeyPair) throws AException;
}
