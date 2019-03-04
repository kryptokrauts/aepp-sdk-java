package com.kryptokrauts.aeternity.sdk.service.keypair;

import com.kryptokrauts.aeternity.sdk.domain.secret.impl.BaseKeyPair;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.MnemonicKeyPair;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.RawKeyPair;
import com.kryptokrauts.aeternity.sdk.exception.AException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.bitcoinj.crypto.ChildNumber;

public interface KeyPairService {

  /** @return a base58 encoded keypair */
  BaseKeyPair generateBaseKeyPair();

  /** @return a byte arrayed keypair */
  RawKeyPair generateRawKeyPair();

  /**
   * @param privateKey
   * @return a base58 encoded keypair
   */
  BaseKeyPair generateBaseKeyPairFromSecret(String privateKey);

  /**
   * @param privateKey
   * @return a raw keypair
   */
  RawKeyPair generateRawKeyPairFromSecret(String privateKey);

  /**
   * encrypts the privateKey using the given password
   *
   * @param password
   * @param binaryKey
   * @return
   * @throws NoSuchPaddingException
   * @throws IllegalBlockSizeException
   * @throws BadPaddingException
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeyException
   */
  byte[] encryptPrivateKey(String password, byte[] binaryKey)
      throws NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException,
          NoSuchAlgorithmException, InvalidKeyException;

  /**
   * encrypts the publicKey using the given password
   *
   * @param password
   * @param binaryKey
   * @return
   * @throws NoSuchPaddingException
   * @throws IllegalBlockSizeException
   * @throws BadPaddingException
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeyException
   */
  byte[] encryptPublicKey(String password, byte[] binaryKey)
      throws NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException,
          NoSuchAlgorithmException, InvalidKeyException;

  /**
   * decrypts the privateKey using the given password
   *
   * @param password
   * @param encryptedBinaryKey
   * @return
   * @throws NoSuchPaddingException
   * @throws UnsupportedEncodingException
   * @throws IllegalBlockSizeException
   * @throws BadPaddingException
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeyException
   */
  byte[] decryptPrivateKey(String password, byte[] encryptedBinaryKey)
      throws NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException,
          BadPaddingException, NoSuchAlgorithmException, InvalidKeyException;

  /**
   * decrypts the publicKey using the given password
   *
   * @param password
   * @param encryptedBinaryKey
   * @return
   * @throws NoSuchPaddingException
   * @throws IllegalBlockSizeException
   * @throws BadPaddingException
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeyException
   */
  byte[] decryptPublicKey(String password, byte[] encryptedBinaryKey)
      throws NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException,
          NoSuchAlgorithmException, InvalidKeyException;

  /**
   * encrypts the public and private key of the given rawKeyPair using the given password
   *
   * @param keyPairRaw
   * @param password
   * @return a rawKeyPair object containing the encrypted byte arrays
   * @throws IllegalBlockSizeException
   * @throws InvalidKeyException
   * @throws BadPaddingException
   * @throws NoSuchAlgorithmException
   * @throws NoSuchPaddingException
   */
  RawKeyPair encryptRawKeyPair(RawKeyPair keyPairRaw, String password)
      throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException,
          NoSuchAlgorithmException, NoSuchPaddingException;

  /**
   * creates a keyPair as well as a list of mnemonic seed words which can be used to restore the key
   * (derived from BIP32/39 HD-Wallet generation) the number of seed words depends on the parameter
   * defined in {@link KeyPairServiceConfiguration}
   *
   * @param mnemonicSeedPassword password or null which is used to seed the list of mnemonics
   * @return keypair with private and public key as well as the generated list of mnemonic seed
   *     words
   * @throws KeyPairGenerationException
   */
  MnemonicKeyPair generateMasterMnemonicKeyPair(String mnemonicSeedPassword) throws AException;

  /**
   * recover keypair from given mnemonic seed word list with given seed password
   *
   * @param mnemonicSeedWords
   * @param mnemonicSeedPassword
   * @return
   * @throws AException
   */
  MnemonicKeyPair recoverMasterMnemonicKeyPair(
      List<String> mnemonicSeedWords, String mnemonicSeedPassword) throws AException;

  /**
   * get a key derived from the master key passed within the mnemonicKeyPair the derived keys are
   * generated according to the deterministic tree stated in <a
   * href=https://github.com/bitcoin/bips/blob/master/bip-0032.mediawiki#Master_key_generation>BIP32</a>
   *
   * @param mnemonicKeyPair mnemonicKeyPair containing the masterKey
   * @param hardened Given a hardened key, it is not possible to derive a child public key if you
   *     know only the hardened public key. For Further information see BIP32 and {@link
   *     ChildNumber}
   * @param derivationPath a custom path for derivating child key. this path will be appended to the
   *     aeternity base path "m/44h/457h" and has to be passed as list of ChildNumbers, beginning
   *     from the coin level, appended subsequently
   * @return a new mnemonic object containing the derived child key according to the hierarchical
   *     tree
   * @throws AException
   */
  MnemonicKeyPair generateDerivedKey(
      MnemonicKeyPair mnemonicKeyPair, boolean hardened, ChildNumber... derivationPath)
      throws AException;
}
