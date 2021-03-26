package com.kryptokrauts.aeternity.sdk.service.keypair.impl;

import static com.kryptokrauts.aeternity.sdk.util.ByteUtils.leftPad;
import static com.kryptokrauts.aeternity.sdk.util.ByteUtils.rightPad;

import com.kryptokrauts.aeternity.sdk.constants.ApiIdentifiers;
import com.kryptokrauts.aeternity.sdk.constants.BaseConstants;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.Account;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.DeterministicHierarchy;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.MnemonicKeyPair;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.RawKeyPair;
import com.kryptokrauts.aeternity.sdk.exception.AException;
import com.kryptokrauts.aeternity.sdk.service.keypair.KeyPairService;
import com.kryptokrauts.aeternity.sdk.service.keypair.KeyPairServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.util.CryptoUtils;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.crypto.HDUtils;
import org.bitcoinj.crypto.MnemonicCode;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator;
import org.bouncycastle.crypto.params.Ed25519KeyGenerationParameters;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public final class KeyPairServiceImpl implements KeyPairService {
  private static final Logger _logger = LoggerFactory.getLogger(KeyPairServiceImpl.class);

  private static final SecureRandom secureRandom = new SecureRandom();

  @Nonnull private KeyPairServiceConfiguration config;

  @Override
  public MnemonicKeyPair generateMasterMnemonicKeyPair(String mnemonicSeedPassword)
      throws AException {
    try {
      if (mnemonicSeedPassword == null) {
        mnemonicSeedPassword = "";
      }
      // generate a random byte array
      byte[] entropy = CryptoUtils.generateSalt(config.getEntropySizeInByte());
      // generate the list of mnemonic seed words based on the random byte array
      List<String> mnemonicSeedWords = MnemonicCode.INSTANCE.toMnemonic(entropy);
      return recoverMasterMnemonicKeyPair(mnemonicSeedWords, mnemonicSeedPassword);
    } catch (Exception e) {
      throw new AException(
          String.format("An error occured generating keyPair %s", e.getLocalizedMessage()), e);
    }
  }

  @Override
  public MnemonicKeyPair recoverMasterMnemonicKeyPair(
      List<String> mnemonicSeedWords, String mnemonicSeedPassword) throws AException {

    if (mnemonicSeedPassword == null) {
      mnemonicSeedPassword = "";
    }
    // generate the seed from words and password
    byte[] seed = MnemonicCode.toSeed(mnemonicSeedWords, mnemonicSeedPassword);

    MnemonicKeyPair masterMnenomicKeypair =
        new MnemonicKeyPair(this.generateMasterKeyFromSeed(seed), mnemonicSeedWords);

    /**
     * following the BIP32 specification create the following derivation path: purpose (44) -> coin
     * (457) -> account (0=master) -> external chain -> child address. The hierarchical tree has the
     * following structure m'/44'/457'/(i = child number)'/0'/0'
     */
    masterMnenomicKeypair
        .getDeterministicHierarchy()
        .addAccount(
            deriveChild(
                BaseConstants.HD_CHAIN_PURPOSE,
                masterMnenomicKeypair.getDeterministicHierarchy().getMasterKey()));
    masterMnenomicKeypair
        .getDeterministicHierarchy()
        .addChain(
            deriveChild(
                BaseConstants.HD_CHAIN_CODE_AETERNITY,
                masterMnenomicKeypair.getDeterministicHierarchy().getAccountKeypair()));
    deriveNextAccount(masterMnenomicKeypair);
    return masterMnenomicKeypair;
  }

  private RawKeyPair generateMasterKeyFromSeed(byte[] seed) {
    byte[] i = HDUtils.hmacSha512("ed25519 seed".getBytes(), seed);

    byte[] masterKey = Arrays.copyOfRange(i, 0, 32);
    byte[] chainCode = Arrays.copyOfRange(i, 32, 64);

    return new RawKeyPair(chainCode, masterKey);
  }

  /**
   * derives a child from given parent at position index
   *
   * @param index the index number of the child to derive
   * @param parent keypair to derive from
   * @return derive child
   */
  private RawKeyPair deriveChild(int index, RawKeyPair parent) {
    ByteBuffer buffer = ByteBuffer.allocate(37);
    buffer.put((byte) 0);

    byte[] privateKey32 = parent.getPrivateKey();
    if (privateKey32.length == 64) {
      privateKey32 = Arrays.copyOfRange(privateKey32, 0, 32);
    }
    buffer.put(privateKey32);
    // we always generate a hardened key
    buffer.putInt(index + 0x80000000);

    byte[] I = HDUtils.hmacSha512(parent.getPublicKey(), buffer.array());
    // chaincode
    byte[] il = Arrays.copyOfRange(I, 0, 32);
    // private key
    byte[] ir = Arrays.copyOfRange(I, 32, 64);

    if (_logger.isTraceEnabled()) {
      print(il, ir);
    }

    return new RawKeyPair(ir, il);
  }

  public String byteToHex(byte[] key) {
    return new String(Hex.encode(key));
  }

  public void print(byte[] il, byte[] ir) {
    _logger.trace("Child private key: " + byteToHex(il));
    _logger.trace("Child chaincode: " + byteToHex(ir));

    Account bkp = generateAccountFromSecret(byteToHex(il));

    _logger.trace("Private key for account: " + bkp.getPrivateKey());
    _logger.trace("Address key for account: " + bkp.getAddress() + "\n");
  }

  // return Raw
  public RawKeyPair deriveNextRawKeyPair(MnemonicKeyPair mnemonicKeyPair) throws AException {
    RawKeyPair miKeypair =
        deriveChild(
            mnemonicKeyPair.getDeterministicHierarchy().getNextChildIndex(),
            mnemonicKeyPair.getDeterministicHierarchy().getChainKeypair());
    RawKeyPair mi0Keypair = deriveChild(DeterministicHierarchy.ADDRESS_INDEX_DEFAULT, miKeypair);
    RawKeyPair mi00Keypair = deriveChild(DeterministicHierarchy.ADDRESS_INDEX_DEFAULT, mi0Keypair);
    mnemonicKeyPair.getDeterministicHierarchy().addNextAddress(miKeypair, mi0Keypair, mi00Keypair);
    return mnemonicKeyPair.getLastGeneratedChild();
  }

  @Override
  public Account deriveNextAccount(MnemonicKeyPair mnemonicKeyPair) throws AException {
    return this.generateAccountFromSecret(
        byteToHex(this.deriveNextRawKeyPair(mnemonicKeyPair).getPrivateKey()));
  }

  @Override
  public Account generateAccount() {
    RawKeyPair rawKeyPair = generateKeyPairInternal();
    byte[] publicKey = rawKeyPair.getPublicKey();
    byte[] privateKey = rawKeyPair.getPrivateKey();
    String aePublicKey = EncodingUtils.encodeCheck(publicKey, ApiIdentifiers.ACCOUNT_PUBKEY);
    String privateKeyHex = Hex.toHexString(privateKey) + Hex.toHexString(publicKey);
    return Account.builder().address(aePublicKey).privateKey(privateKeyHex).build();
  }

  @Override
  public RawKeyPair generateRawKeyPair() {
    RawKeyPair rawKeyPair = generateKeyPairInternal();
    byte[] publicKey = rawKeyPair.getPublicKey();
    byte[] privateKey = rawKeyPair.getPrivateKey();
    return RawKeyPair.builder().publicKey(publicKey).privateKey(privateKey).build();
  }

  @Override
  public Account generateAccountFromSecret(String privateKey) {
    final String privateKey32;
    if (privateKey.length() == 128) {
      privateKey32 = privateKey.substring(0, 64);
    } else {
      privateKey32 = privateKey;
    }
    Ed25519PrivateKeyParameters privateKeyParams =
        new Ed25519PrivateKeyParameters(Hex.decode(privateKey32), 0);
    Ed25519PublicKeyParameters publicKeyParams = privateKeyParams.generatePublicKey();
    byte[] publicBinary = publicKeyParams.getEncoded();
    byte[] privateBinary = privateKeyParams.getEncoded();
    String aePublicKey = EncodingUtils.encodeCheck(publicBinary, ApiIdentifiers.ACCOUNT_PUBKEY);
    String privateKeyHex = Hex.toHexString(privateBinary) + Hex.toHexString(publicBinary);
    return Account.builder().address(aePublicKey).privateKey(privateKeyHex).build();
  }

  @Override
  public Account toAccount(RawKeyPair rawKeyPair) {
    return generateAccountFromSecret(Hex.toHexString(rawKeyPair.getPrivateKey()));
  }

  /**
   * the actual keypair generation method
   *
   * @return the raw byte arrays for private and public key
   */
  private RawKeyPair generateKeyPairInternal() {
    Ed25519KeyPairGenerator keyPairGenerator = new Ed25519KeyPairGenerator();
    keyPairGenerator.init(new Ed25519KeyGenerationParameters(secureRandom));
    AsymmetricCipherKeyPair asymmetricCipherKeyPair = keyPairGenerator.generateKeyPair();
    Ed25519PublicKeyParameters publicKeyParams =
        (Ed25519PublicKeyParameters) asymmetricCipherKeyPair.getPublic();
    Ed25519PrivateKeyParameters privateKeyParams =
        (Ed25519PrivateKeyParameters) asymmetricCipherKeyPair.getPrivate();
    byte[] publicKey = publicKeyParams.getEncoded();
    byte[] privateKey = privateKeyParams.getEncoded();
    return RawKeyPair.builder().publicKey(publicKey).privateKey(privateKey).build();
  }

  @Override
  public RawKeyPair generateRawKeyPairFromSecret(final String privateKey) {
    final String privateKey32;
    if (privateKey.length() == 128) {
      privateKey32 = privateKey.substring(0, 64);
    } else {
      privateKey32 = privateKey;
    }
    Ed25519PrivateKeyParameters privateKeyParams =
        new Ed25519PrivateKeyParameters(Hex.decode(privateKey32), 0);
    Ed25519PublicKeyParameters publicKeyParams = privateKeyParams.generatePublicKey();
    byte[] publicBinary = publicKeyParams.getEncoded();
    byte[] privateBinary = privateKeyParams.getEncoded();
    return RawKeyPair.builder().publicKey(publicBinary).privateKey(privateBinary).build();
  }

  @Override
  public final byte[] encryptPrivateKey(final String password, final byte[] binaryKey)
      throws NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException,
          NoSuchAlgorithmException, InvalidKeyException {
    return encryptKey(password, leftPad(64, binaryKey));
  }

  @Override
  public final byte[] encryptPublicKey(final String password, final byte[] binaryKey)
      throws NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException,
          NoSuchAlgorithmException, InvalidKeyException {
    return encryptKey(password, rightPad(32, binaryKey));
  }

  @Override
  public final byte[] decryptPrivateKey(final String password, final byte[] encryptedBinaryKey)
      throws NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException,
          NoSuchAlgorithmException, InvalidKeyException {
    return decryptKey(password, encryptedBinaryKey);
  }

  @Override
  public final byte[] decryptPublicKey(final String password, final byte[] encryptedBinaryKey)
      throws NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException,
          NoSuchAlgorithmException, InvalidKeyException {
    return Arrays.copyOfRange(decryptKey(password, encryptedBinaryKey), 0, 32);
  }

  @Override
  public RawKeyPair encryptRawKeyPair(final RawKeyPair keyPairRaw, final String password)
      throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException,
          NoSuchAlgorithmException, NoSuchPaddingException {
    byte[] encryptedPublicKey = encryptPublicKey(password, keyPairRaw.getPublicKey());
    byte[] encryptedPrivateKey = encryptPrivateKey(password, keyPairRaw.getPrivateKey());
    return RawKeyPair.builder()
        .publicKey(encryptedPublicKey)
        .privateKey(encryptedPrivateKey)
        .build();
  }

  private final byte[] encryptKey(final String password, final byte[] binaryData)
      throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
          IllegalBlockSizeException, BadPaddingException {
    byte[] hashedPassword = Sha256Hash.hash(password.getBytes());
    Cipher cipher = Cipher.getInstance(config.getCipherAlgorithm());
    SecretKey secretKey = new SecretKeySpec(hashedPassword, config.getSecretKeySpec());
    cipher.init(Cipher.ENCRYPT_MODE, secretKey);
    return cipher.doFinal(binaryData);
  }

  private final byte[] decryptKey(final String password, final byte[] encryptedBinaryData)
      throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
          BadPaddingException, IllegalBlockSizeException {
    byte[] hashedPassword = Sha256Hash.hash(password.getBytes());
    Cipher cipher = Cipher.getInstance(config.getCipherAlgorithm());
    SecretKey secretKey = new SecretKeySpec(hashedPassword, config.getSecretKeySpec());
    cipher.init(Cipher.DECRYPT_MODE, secretKey);
    return cipher.doFinal(encryptedBinaryData);
  }
}
