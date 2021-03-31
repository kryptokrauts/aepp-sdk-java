package com.kryptokrauts.aeternity.sdk.service.keypair;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.greghaskins.spectrum.Spectrum;
import com.kryptokrauts.aeternity.sdk.BaseTest;
import com.kryptokrauts.aeternity.sdk.domain.secret.HDWallet;
import com.kryptokrauts.aeternity.sdk.exception.AException;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Assert;

public class KeyPairServiceFactoryTest extends BaseTest {
  {
    Spectrum.describe(
        "Keypair Service Factory",
        () -> {
          Spectrum.it(
              " returns same instance for configuration",
              () -> {
                KeyPairServiceFactory keypairServiceFactory = new KeyPairServiceFactory();

                KeyPairServiceConfiguration config =
                    KeyPairServiceConfiguration.configure()
                        .cipherAlgorithm("Blowfish")
                        .secretKeySpec("PBKDF2WithHmacSHA1")
                        .compile();

                KeyPairService firstGet = keypairServiceFactory.getService(config);
                KeyPairService secondGet = keypairServiceFactory.getService(config);
                Assert.assertEquals(secondGet, firstGet);
              });
        });

    Spectrum.describe(
        "Mnemonic keypair generation tests",
        () -> {
          final String mnemonicSeedPassword = "kryptokrauts";

          KeyPairService keyPairService = new KeyPairServiceFactory().getService();
          HDWallet generatedKeyPair =
              keyPairService.generateMasterMnemonicKeyPair(mnemonicSeedPassword);
          HDWallet restoredKeyPairWithSamePWD =
              keyPairService.recoverMasterMnemonicKeyPair(
                  generatedKeyPair.getMnemonicSeedWords(), mnemonicSeedPassword);
          HDWallet restoredKeyPairWithoutPWD =
              keyPairService.recoverMasterMnemonicKeyPair(
                  generatedKeyPair.getMnemonicSeedWords(), null);

          Spectrum.it(
              "mnemonic keypair recovered from word seed list is same",
              () -> {
                Assert.assertEquals(
                    Hex.toHexString(generatedKeyPair.getMasterKeyPair().getRawPrivateKey()),
                    Hex.toHexString(
                        restoredKeyPairWithSamePWD.getMasterKeyPair().getRawPrivateKey()));
              });
          Spectrum.it(
              "mnemonic keypair recovered from word seed list without password is not same",
              () -> {
                Assert.assertNotEquals(
                    Hex.toHexString(generatedKeyPair.getMasterKeyPair().getRawPrivateKey()),
                    Hex.toHexString(
                        restoredKeyPairWithoutPWD.getMasterKeyPair().getRawPrivateKey()));
              });
          Spectrum.it(
              "mnemonic keypair cannot be generated due to small entropy",
              () -> {
                KeyPairService keyPairServiceWrongConfig =
                    new KeyPairServiceFactory()
                        .getService(
                            KeyPairServiceConfiguration.configure().entropySizeInByte(2).compile());
                assertThrows(
                    AException.class,
                    () -> {
                      keyPairServiceWrongConfig.generateMasterMnemonicKeyPair(null);
                    });
              });
        });
  }
}
