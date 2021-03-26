package com.kryptokrauts.aeternity.sdk.service.keypair;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.greghaskins.spectrum.Spectrum;
import com.kryptokrauts.aeternity.sdk.BaseTest;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.Account;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.MnemonicKeyPair;
import com.kryptokrauts.aeternity.sdk.exception.AException;
import com.kryptokrauts.aeternity.sdk.service.keypair.Slip0100JsonStruct.DerivedKeyEntry;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Assert;

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

                      String firtChildPrivateKeyAsHex =
                          "cb9b31d8e09182ab7228a3252984a68c41513cea33283313680f34c382c58127769cd5368a1a498dff86d3aec54ff4eb3ac89c13d6b100461907133bfa5cd082";
                      String firstChildAddress =
                          "ak_uEoRY2CEAbSzMbN4ohniNHFdrVDHjpw3Za3upmJA8SihGBjCk";

                      MnemonicKeyPair restoredDefault =
                          keyPairService.recoverMasterMnemonicKeyPair(mnemonic, defaultPassword);

                      Account restoredAccount =
                          keyPairService.generateAccountFromSecret(
                              Hex.toHexString(restoredDefault.getChildAt(0).getPrivateKey()));

                      Assert.assertEquals(firstChildAddress, restoredAccount.getAddress());
                      Assert.assertEquals(
                          firtChildPrivateKeyAsHex, restoredAccount.getPrivateKey());
                    });
                Spectrum.it(
                    "sample vector 1 recover test",
                    () -> {
                      List<String> mnemonic =
                          Arrays.asList(
                              "acquire useful napkin ranch witness scare lunch smart sibling situate potato inspire");

                      String firtChildPrivateKeyAsHex =
                          "c11b21e4ee83781ad516bddb6a708d95287a2c1a1ef6bf42cc06ac2cf2dbeb490d960a47eeaa08ee4b6cf4b19a14a11411ac65c86663b97cf80335f053a42dfe";
                      String firstChildAddress =
                          "ak_6z3Efre5nVXLB1togCbr2AWrjnKZNWRAXukSdTPPrAAzdFvXC";
                      String secondChildPrivateKeyAsHex =
                          "26b44062afc81f1df04ead8b2700010af8c971399b5cc39f7da42a3dd45047b18cfc84a633b529c17ee5300e295e501b10ffa633c07dee7457aba16703ef00cf";
                      String secondChildAddress =
                          "ak_256JtFuqyGJ8PPTdkvSi9Lg1E1HhsWeEnxVMUU2tWJQLsUUm3v";

                      MnemonicKeyPair restoredDefault =
                          keyPairService.recoverMasterMnemonicKeyPair(mnemonic, "");

                      Account firstChildAccount =
                          keyPairService.generateAccountFromSecret(
                              Hex.toHexString(restoredDefault.getChildAt(0).getPrivateKey()));
                      Account secondChildAccount =
                          keyPairService.deriveNextAccount(restoredDefault);

                      Assert.assertEquals(firstChildAddress, firstChildAccount.getAddress());
                      Assert.assertEquals(
                          firtChildPrivateKeyAsHex, firstChildAccount.getPrivateKey());
                      Assert.assertEquals(secondChildAddress, secondChildAccount.getAddress());
                      Assert.assertEquals(
                          secondChildPrivateKeyAsHex, secondChildAccount.getPrivateKey());
                    });

                Spectrum.it(
                    "slip0010 test vectors",
                    () -> {
                      URL resourceFile =
                          getClass().getClassLoader().getResource("derivedkeys.json");
                      List<Slip0100JsonStruct> slip0100Resources =
                          new ObjectMapper()
                              .readValue(
                                  this.getClass()
                                      .getClassLoader()
                                      .getResourceAsStream("derivedkeys.json"),
                                  new TypeReference<List<Slip0100JsonStruct>>() {});
                      System.out.println(
                          "Generating keys for " + slip0100Resources.size() + " test vectors");
                      for (Slip0100JsonStruct testVector : slip0100Resources) {
                        MnemonicKeyPair restored =
                            keyPairService.recoverMasterMnemonicKeyPair(
                                Arrays.asList(testVector.getMnemonic().split(" ")),
                                testVector.getPassword());
                        testVector
                            .getAccounts()
                            .sort(Comparator.comparing(DerivedKeyEntry::getIndex));
                        for (DerivedKeyEntry derivedKey : testVector.getAccounts()) {
                          Account derivedAccount = null;
                          if (derivedKey.getIndex() == 0) {
                            derivedAccount =
                                keyPairService.toAccount(
                                    restored.getChildAt(derivedKey.getIndex()));
                          } else {
                            derivedAccount = keyPairService.deriveNextAccount(restored);
                          }
                          Assert.assertEquals(derivedKey.getAddress(), derivedAccount.getAddress());
                          Assert.assertEquals(derivedKey.getPk(), derivedAccount.getPrivateKey());
                        }
                      }
                    });
              });
        });
  }
}
