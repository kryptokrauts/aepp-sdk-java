package com.kryptokrauts.aeternity.sdk.keypair;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.Assert;
import org.spongycastle.util.encoders.Hex;

import com.greghaskins.spectrum.Spectrum;
import com.kryptokrauts.aeternity.sdk.BaseTest;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.MnemonicKeyPair;
import com.kryptokrauts.aeternity.sdk.exception.AException;
import com.kryptokrauts.aeternity.sdk.service.keypair.KeyPairService;
import com.kryptokrauts.aeternity.sdk.service.keypair.KeyPairServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.keypair.KeyPairServiceFactory;

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
          MnemonicKeyPair generatedKeyPair =
              keyPairService.generateMasterMnemonicKeyPair(mnemonicSeedPassword);
          MnemonicKeyPair restoredKeyPairWithSamePWD =
              keyPairService.recoverMasterMnemonicKeyPair(
                  generatedKeyPair.getMnemonicSeedWords(), mnemonicSeedPassword);
          MnemonicKeyPair restoredKeyPairWithoutPWD =
              keyPairService.recoverMasterMnemonicKeyPair(
                  generatedKeyPair.getMnemonicSeedWords(), null);

          Spectrum.it(
              "mnemonic keypair recovered from word seed list is same",
              () -> {
                Assert.assertEquals(
                    Hex.toHexString(generatedKeyPair.getPrivateKey()),
                    Hex.toHexString(restoredKeyPairWithSamePWD.getPrivateKey()));
              });
          Spectrum.it(
              "mnemonic keypair recovered from word seed list without password is not same",
              () -> {
                Assert.assertNotEquals(
                    Hex.toHexString(generatedKeyPair.getPrivateKey()),
                    Hex.toHexString(restoredKeyPairWithoutPWD.getPrivateKey()));
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
