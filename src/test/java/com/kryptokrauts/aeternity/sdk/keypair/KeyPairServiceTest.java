package com.kryptokrauts.aeternity.sdk.keypair;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.greghaskins.spectrum.Spectrum;
import com.kryptokrauts.aeternity.sdk.BaseTest;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.BaseKeyPair;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.MnemonicKeyPair;
import com.kryptokrauts.aeternity.sdk.exception.AException;
import com.kryptokrauts.aeternity.sdk.service.keypair.KeyPairService;
import com.kryptokrauts.aeternity.sdk.service.keypair.KeyPairServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.keypair.KeyPairServiceFactory;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import java.util.Arrays;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.junit.Assert;
import org.spongycastle.util.encoders.Hex;

public class KeyPairServiceTest extends BaseTest {
  {
    Spectrum.describe(
        "Keypair Service Test",
        () -> {
          Spectrum.describe(
              "Mnemonic keypair generation tests",
              () -> {
                KeyPairService keyPairService = new KeyPairServiceFactory().getService();
                MnemonicKeyPair generatedKeyPair =
                    keyPairService.generateMasterMnemonicKeyPair(defaultPassword);
                MnemonicKeyPair restoredKeyPairWithSamePWD =
                    keyPairService.recoverMasterMnemonicKeyPair(
                        generatedKeyPair.getMnemonicSeedWords(), defaultPassword);
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
                                  KeyPairServiceConfiguration.configure()
                                      .entropySizeInByte(2)
                                      .compile());
                      assertThrows(
                          AException.class,
                          () -> {
                            keyPairServiceWrongConfig.generateMasterMnemonicKeyPair(null);
                          });
                    });
                Spectrum.it(
                    "default vector recover test",
                    () -> {
                      List<String> mnemonic =
                          Arrays.asList(
                              "abandon", "abandon", "abandon", "abandon", "abandon", "abandon",
                              "abandon", "abandon", "abandon", "abandon", "abandon", "about");

                      String privateKeyAsHex =
                          "c3509e5731235221c9bfea8f90468edbc646c16535119788d34a02ddf9bd72d32e79912cf3443060e3081fa78df1438bd422095a2ea56cf19144fac9c9b8603a";
                      String publicKeyAsHex =
                          "ak_MU91xV8BxuA3Bm4n8kFAqDykDeQJSwEFzHMDnVtnHo2dVtSFq";

                      MnemonicKeyPair restoredDefault =
                          keyPairService.recoverMasterMnemonicKeyPair(mnemonic, defaultPassword);

                      BaseKeyPair restoredBaseKeyPair =
                          EncodingUtils.createBaseKeyPair(restoredDefault.toRawKeyPair());

                      Assert.assertEquals(publicKeyAsHex, restoredBaseKeyPair.getPublicKey());
                      Assert.assertEquals(privateKeyAsHex, restoredBaseKeyPair.getPrivateKey());
                    });
                Spectrum.it(
                    "hd derivation keys restore test",
                    () -> {
                      List<String> mnemonic =
                          Arrays.asList(
                              "legal", "winner", "thank", "year", "wave", "sausage", "worth",
                              "useful", "legal", "winner", "thank", "yellow");

                      MnemonicKeyPair master =
                          keyPairService.recoverMasterMnemonicKeyPair(mnemonic, defaultPassword);

                      MnemonicKeyPair masterNoPWD =
                          keyPairService.recoverMasterMnemonicKeyPair(mnemonic, "");

                      ResourceBundle derivedKeys = ResourceBundle.getBundle("derivedKeys");

                      /**
                       * make sure every that derived keys can be restored and that the hardened
                       * keys differ
                       */
                      for (int i = 0; i < 20; i++) {
                        // derive different keys
                        BaseKeyPair generatedDerivedKey =
                            EncodingUtils.createBaseKeyPair(
                                keyPairService.generateDerivedKey(master, true).toRawKeyPair());
                        BaseKeyPair notHardendedKey =
                            EncodingUtils.createBaseKeyPair(
                                keyPairService.generateDerivedKey(master, false).toRawKeyPair());
                        BaseKeyPair generatedDerivedKeyNoPwd =
                            EncodingUtils.createBaseKeyPair(
                                keyPairService
                                    .generateDerivedKey(masterNoPWD, false)
                                    .toRawKeyPair());
                        Assert.assertEquals(
                            derivedKeys.getObject(generatedDerivedKey.getPublicKey()),
                            generatedDerivedKey.getPrivateKey());

                        // make sure, not hardended keys differ
                        assertThrows(
                            MissingResourceException.class,
                            () -> {
                              derivedKeys.getObject(notHardendedKey.getPublicKey());
                            });
                        // make sure, keys derived from master with same mnemonics but different pwd
                        // are different
                        assertThrows(
                            MissingResourceException.class,
                            () -> {
                              derivedKeys.getObject(generatedDerivedKeyNoPwd.getPublicKey());
                            });
                      }
                    });
                Spectrum.it(
                    "hd derivation keys not possible from derived key test",
                    () -> {
                      List<String> mnemonic =
                          Arrays.asList(
                              "letter",
                              "advice",
                              "cage",
                              "absurd",
                              "amount",
                              "doctor",
                              "acoustic",
                              "avoid",
                              "letter",
                              "advice",
                              "cage",
                              "above");

                      MnemonicKeyPair master =
                          keyPairService.recoverMasterMnemonicKeyPair(mnemonic, defaultPassword);
                      MnemonicKeyPair generatedDerivedKey =
                          keyPairService.generateDerivedKey(master, true);

                      Throwable exception =
                          assertThrows(
                              AException.class,
                              () -> {
                                keyPairService.generateDerivedKey(generatedDerivedKey, true);
                              });
                      Assert.assertTrue(
                          exception
                              .getMessage()
                              .contains(
                                  "Given mnemonicKeyPair object does not contain the master key"));
                    });
              });
        });
  }
}
