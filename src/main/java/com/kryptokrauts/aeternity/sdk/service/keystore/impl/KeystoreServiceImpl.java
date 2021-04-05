package com.kryptokrauts.aeternity.sdk.service.keystore.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kryptokrauts.aeternity.sdk.domain.Keystore;
import com.kryptokrauts.aeternity.sdk.domain.secret.HDWallet;
import com.kryptokrauts.aeternity.sdk.domain.secret.KeyPair;
import com.kryptokrauts.aeternity.sdk.exception.AException;
import com.kryptokrauts.aeternity.sdk.service.keystore.KeystoreService;
import com.kryptokrauts.aeternity.sdk.service.keystore.KeystoreServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.util.CryptoUtils;
import de.mkammerer.argon2.Argon2Advanced;
import de.mkammerer.argon2.Argon2Factory;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.apache.tuweni.crypto.sodium.SecretBox;
import org.bouncycastle.util.encoders.Hex;

@RequiredArgsConstructor
public class KeystoreServiceImpl implements KeystoreService {

  private static final String HD_WALLET_IDENTIFIER = "___hdwallet___";

  @Nonnull private KeystoreServiceConfiguration config;

  @Override
  public String createHDKeystore(HDWallet hdWallet, String keystorePassword) throws AException {
    return createKeystoreFile(
        String.join("_", hdWallet.getMnemonicSeedWords()).getBytes(),
        keystorePassword,
        HD_WALLET_IDENTIFIER,
        hdWallet.getMasterKeyPair().getAddress());
  }

  @Override
  public List<String> recoverHDKeystore(String keystoreJSON, String keystorePassword)
      throws AException {
    checkHDKeystoreFile(keystoreJSON);
    return Arrays.asList(
        new String(recoverKeystoreFile(keystoreJSON, keystorePassword)).split("_"));
  }

  @Override
  public String createKeystore(KeyPair keyPair, String keystorePassword, String keystoreName)
      throws AException {
    // chain public and private key byte arrays
    byte[] privateAndPublicKey =
        new byte[keyPair.getRawPrivateKey().length + keyPair.getRawPublicKey().length];
    System.arraycopy(
        keyPair.getRawPrivateKey(), 0, privateAndPublicKey, 0, keyPair.getRawPrivateKey().length);
    System.arraycopy(
        keyPair.getRawPublicKey(),
        0,
        privateAndPublicKey,
        keyPair.getRawPrivateKey().length,
        keyPair.getRawPublicKey().length);
    return createKeystoreFile(
        privateAndPublicKey, keystorePassword, keystoreName, keyPair.getAddress());
  }

  @Override
  public String recoverPrivateKeyFromKeystore(String keystoreJSON, String keystorePassword)
      throws AException {
    return new String(Hex.encode(recoverKeystoreFile(keystoreJSON, keystorePassword)));
  }

  private byte[] recoverKeystoreFile(String keystoreJSON, String keystorePassword) {
    try {
      Keystore recoveredKeystore = new ObjectMapper().readValue(keystoreJSON, Keystore.class);
      Argon2Advanced argon2Advanced = Argon2Factory.createAdvanced(config.getArgon2Type());
      // extract salt
      byte[] salt = Hex.decode(recoveredKeystore.getCrypto().getKdfParams().getSalt());
      // generate hash from password
      byte[] rawHash =
          argon2Advanced.rawHash(
              config.getOpsLimit(),
              config.getMemlimitKIB(),
              config.getParallelism(),
              keystorePassword.toCharArray(),
              StandardCharsets.UTF_8,
              salt);
      // extract nonce
      byte[] nonce = Hex.decode(recoveredKeystore.getCrypto().getCipherParams().getNonce());

      // extract cipertext
      byte[] ciphertext = Hex.decode(recoveredKeystore.getCrypto().getCipherText());

      // recover private key
      byte[] decrypted =
          SecretBox.decrypt(
              ciphertext, SecretBox.Key.fromBytes(rawHash), SecretBox.Nonce.fromBytes(nonce));
      if (decrypted == null) {
        throw new AException("Error recovering keystore file - wrong password");
      }
      return decrypted;
    } catch (IOException e) {
      throw new AException("Error recovering keystore-json", e);
    }
  }

  private String createKeystoreFile(
      byte[] valueToEncrypt, String keystorePassword, String keystoreName, String address)
      throws AException {
    // create derived key with Argon2
    Argon2Advanced argon2Advanced = Argon2Factory.createAdvanced(config.getArgon2Type());
    byte[] salt = CryptoUtils.generateSalt(config.getDefaultSaltLength());

    // generate hash from password
    byte[] rawHash =
        argon2Advanced.rawHash(
            config.getOpsLimit(),
            config.getMemlimitKIB(),
            config.getParallelism(),
            keystorePassword.toCharArray(),
            StandardCharsets.UTF_8,
            salt);

    // encrypt the key arrays with nonce and derived key
    SecretBox.Nonce nonce = SecretBox.Nonce.random();
    byte[] ciphertext = SecretBox.encrypt(valueToEncrypt, SecretBox.Key.fromBytes(rawHash), nonce);

    // generate keystoreName if not given
    if (keystoreName == null || keystoreName.trim().length() == 0) {
      keystoreName = "generated keystore file -" + new Timestamp(System.currentTimeMillis());
    }

    // generate the domain object for keystore
    Keystore keystore =
        Keystore.builder()
            .publicKey(address)
            .crypto(
                Keystore.Crypto.builder()
                    .secretType(config.getSecretType())
                    .symmetricAlgorithm(config.getSymmetricAlgorithm())
                    .cipherText(Hex.toHexString(ciphertext))
                    .cipherParams(
                        Keystore.CipherParams.builder()
                            .nonce(Hex.toHexString(nonce.bytesArray()))
                            .build())
                    .kdf(config.getArgon2Mode())
                    .kdfParams(
                        Keystore.KdfParams.builder()
                            .memLimitKib(config.getMemlimitKIB())
                            .opsLimit(config.getOpsLimit())
                            .salt(Hex.toHexString(salt))
                            .parallelism(config.getParallelism())
                            .build())
                    .build())
            .id(UUID.randomUUID().toString())
            .name(keystoreName)
            .version(config.getVersion())
            .build();

    try {
      return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(keystore);
    } catch (JsonProcessingException e) {
      throw new AException("Error creating keystore-json", e);
    }
  }

  private void checkHDKeystoreFile(String keystoreJSON) throws AException {
    try {
      Keystore recoverWallet = new ObjectMapper().readValue(keystoreJSON, Keystore.class);
      if (!recoverWallet.getName().equals(HD_WALLET_IDENTIFIER)) {
        throw new AException("Given JSON is not a HDWallet keystore");
      }
    } catch (IOException e) {
      throw new AException("Error recovering keystore-json", e);
    }
  }
}
