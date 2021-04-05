package com.kryptokrauts.aeternity.sdk.service.keypair;

import com.kryptokrauts.aeternity.sdk.domain.secret.HDKeyPair;
import com.kryptokrauts.aeternity.sdk.domain.secret.HDWallet;
import com.kryptokrauts.aeternity.sdk.domain.secret.KeyPair;
import com.kryptokrauts.aeternity.sdk.exception.AException;
import java.util.List;

public interface KeyPairService {

  /** @return a byte arrayed keypair */
  KeyPair generateKeyPair();

  /**
   * @param privateKey private key (hex)
   * @return the recovered keypair
   */
  KeyPair recoverKeyPair(String privateKey);

  /**
   * creates a keyPair as well as a list of mnemonic seed words wrapped into HDWallet object, which
   * can be used to restore the key (derived from BIP32/39 HD-Wallet generation) the number of seed
   * words depends on the parameter defined in {@link KeyPairServiceConfiguration}
   *
   * @param mnemonicSeedPassword password or null which is used to seed the list of mnemonics
   * @return keypair with private and public key as well as the generated list of mnemonic seed
   *     words wrapped into {@link HDWallet}
   * @throws AException in case of an error
   */
  HDWallet generateHDWallet(String mnemonicSeedPassword) throws AException;

  /**
   * See {@link #generateHDWallet(String)}
   *
   * @return keypair with private and public key as well as the generated list of mnemonic seed
   *     words wrapped into {@link HDWallet}
   * @throws AException in case of an error
   */
  HDWallet generateHDWallet() throws AException;

  /**
   * recover keypair from given mnemonic seed word list with given seed password
   *
   * @param mnemonicSeedWords the words to recover the HDwallet
   * @param mnemonicSeedPassword the password that protects the keypair(s) generated with the seed
   *     phrase (can be null)
   * @return instance of {@link HDWallet}
   * @throws AException in case of an error
   */
  HDWallet recoverHDWallet(List<String> mnemonicSeedWords, String mnemonicSeedPassword)
      throws AException;

  /**
   * See {@link #recoverHDWallet(List, String)}
   *
   * @param mnemonicSeedWords the words to recover the HDwallet
   * @return instance of {@link HDWallet}
   * @throws AException if recover hdWallet fails
   */
  HDWallet recoverHDWallet(List<String> mnemonicSeedWords) throws AException;

  /**
   * derives the next hardened key. The derived keys are generated according to the deterministic
   * tree saved within the given menomincKeyPair stated in <a
   * href=https://github.com/bitcoin/bips/blob/master/bip-0032.mediawiki#Master_key_generation>BIP32</a>
   *
   * @param hdWallet mnemonicKeyPair containing the deterministic tree of keys necessary for
   *     derivation
   * @return a new derived child raw keypair
   * @throws AException in case of an error
   */
  HDKeyPair getNextKeyPair(HDWallet hdWallet) throws AException;
}
