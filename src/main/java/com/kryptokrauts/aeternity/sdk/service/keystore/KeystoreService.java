package com.kryptokrauts.aeternity.sdk.service.keystore;

import com.kryptokrauts.aeternity.sdk.domain.Keystore;
import com.kryptokrauts.aeternity.sdk.domain.secret.HdWallet;
import com.kryptokrauts.aeternity.sdk.domain.secret.KeyPair;
import com.kryptokrauts.aeternity.sdk.exception.AException;
import java.util.List;

public interface KeystoreService {

  /**
   * create a JSON keystore which can be stored in a file for later recovery of the private key
   *
   * @param keyPair the public / private keypair
   * @param keystorePassword the password for symmetric encryption
   * @param keystoreName the name of the keystore wallet
   * @return encrypts the keypair using the given walletPassword and returns a JSON derived from
   *     {@link Keystore}
   * @throws AException if an error occurs
   */
  String createKeystore(KeyPair keyPair, String keystorePassword, String keystoreName)
      throws AException;

  /**
   * allows to recover a private key from a given keystore json
   *
   * @param keystoreJSON the keystore JSON derived from {@link Keystore}
   * @param keystorePassword the symmetric password used to create the keystore
   * @return encoded private key
   * @throws AException if an error occurs
   */
  String recoverEncodedPrivateKey(String keystoreJSON, String keystorePassword) throws AException;

  /**
   * stores public key and mnemonic seed words (but not the password!) as JSON
   *
   * @param hdWallet instance of {@link HdWallet}
   * @param keystorePassword keystorePassword the symmetric password used to create the keystore
   * @return encrypts the HDWallets seed word list using the given walletPassword and returns a JSON
   *     derived from {@link Keystore}
   * @throws AException if an error occurs
   */
  String createKeystore(HdWallet hdWallet, String keystorePassword) throws AException;

  /**
   * allows to recover the mnemnonic seed words from a given keystore json
   *
   * @param keystoreJSON the keystore JSON derived from {@link Keystore}
   * @param keystorePassword the symmetric password used to create the keystore
   * @return list of mnemonic seed words
   * @throws AException if an error occurs
   */
  List<String> recoverMnemonicSeedWords(String keystoreJSON, String keystorePassword)
      throws AException;
}
