package com.kryptokrauts.aeternity.sdk.service.keypair.impl;

import static com.kryptokrauts.aeternity.sdk.util.ByteUtils.leftPad;
import static com.kryptokrauts.aeternity.sdk.util.ByteUtils.rightPad;

import com.google.common.collect.ImmutableList;
import com.kryptokrauts.aeternity.sdk.constants.ApiIdentifiers;
import com.kryptokrauts.aeternity.sdk.constants.BaseConstants;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.BaseKeyPair;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.MnemonicKeyPair;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.RawKeyPair;
import com.kryptokrauts.aeternity.sdk.exception.AException;
import com.kryptokrauts.aeternity.sdk.service.keypair.KeyPairService;
import com.kryptokrauts.aeternity.sdk.service.keypair.KeyPairServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.util.CryptoUtils;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.LinkedList;
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
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicHierarchy;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
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
    // generate the master key from the seed using bitcoinj implementation of hd
    // wallets
    DeterministicKey master = HDKeyDerivation.createMasterPrivateKey(seed);
    RawKeyPair generatedKeyPair = generateRawKeyPairFromSecret(master.getPrivateKeyAsHex());

    return new MnemonicKeyPair(
        generatedKeyPair, mnemonicSeedWords, new DeterministicHierarchy(master));
  }

  @Override
  public MnemonicKeyPair generateDerivedKey(
      MnemonicKeyPair mnemonicKeyPair, boolean hardened, ChildNumber... derivationPath)
      throws AException {
    DeterministicKey master = mnemonicKeyPair.getDeterministicHierarchy().getRootKey();
    // check if we really have the masterKey at hand
    if (master.getDepth() != 0) {
      throw new AException("Given mnemonicKeyPair object does not contain the master key");
    }
    /**
     * following the BIP32 specification create the following tree purpose -> coin -> account ->
     * external chain -> child address
     */

    /**
     * always set path for purpose {@link BaseConstants.HD_CHAIN_PURPOSE}, coin {@link
     * BaseConstants.HD_CHAIN_CODE_AETERNITY}
     */
    List<ChildNumber> pathToDerivedKey = new LinkedList<ChildNumber>();
    pathToDerivedKey.addAll(
        Arrays.asList(
            new ChildNumber(BaseConstants.HD_CHAIN_PURPOSE, true),
            new ChildNumber(BaseConstants.HD_CHAIN_CODE_AETERNITY, true)));

    /** if no arguments are given, set default account and external chain (0h, 0h) */
    if (derivationPath == null || derivationPath.length == 0) {
      pathToDerivedKey.addAll(Arrays.asList(new ChildNumber(0, true), new ChildNumber(0, true)));
      /** in case arguments are given - add warning */
    } else {
      _logger.warn(
          String.format(
              "You are using a custom key derivation path - this will be appended to m/%sh/%sh",
              BaseConstants.HD_CHAIN_PURPOSE, BaseConstants.HD_CHAIN_CODE_AETERNITY));
      pathToDerivedKey.addAll(Arrays.asList(derivationPath));
    }

    DeterministicKey nextChildDeterministicKey =
        mnemonicKeyPair
            .getDeterministicHierarchy()
            .deriveNextChild(ImmutableList.copyOf(pathToDerivedKey), false, true, hardened);

    // derive a new child
    RawKeyPair childRawKeyPair =
        generateRawKeyPairFromSecret(nextChildDeterministicKey.getPrivateKeyAsHex());

    return new MnemonicKeyPair(
        childRawKeyPair,
        mnemonicKeyPair.getMnemonicSeedWords(),
        new DeterministicHierarchy(nextChildDeterministicKey));
  }

  @Override
  public BaseKeyPair generateBaseKeyPair() {
    RawKeyPair rawKeyPair = generateKeyPairInternal();
    byte[] publicKey = rawKeyPair.getPublicKey();
    byte[] privateKey = rawKeyPair.getPrivateKey();
    String aePublicKey = EncodingUtils.encodeCheck(publicKey, ApiIdentifiers.ACCOUNT_PUBKEY);
    String privateKeyHex = Hex.toHexString(privateKey) + Hex.toHexString(publicKey);
    return BaseKeyPair.builder().publicKey(aePublicKey).privateKey(privateKeyHex).build();
  }

  @Override
  public RawKeyPair generateRawKeyPair() {
    RawKeyPair rawKeyPair = generateKeyPairInternal();
    byte[] publicKey = rawKeyPair.getPublicKey();
    byte[] privateKey = rawKeyPair.getPrivateKey();
    return RawKeyPair.builder().publicKey(publicKey).privateKey(privateKey).build();
  }

  @Override
  public BaseKeyPair generateBaseKeyPairFromSecret(String privateKey) {
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
    return BaseKeyPair.builder().publicKey(aePublicKey).privateKey(privateKeyHex).build();
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
