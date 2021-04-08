package com.kryptokrauts.aeternity.sdk.service.keypair;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.greghaskins.spectrum.Spectrum;
import com.kryptokrauts.aeternity.sdk.BaseTest;
import com.kryptokrauts.aeternity.sdk.domain.secret.HDWallet;
import com.kryptokrauts.aeternity.sdk.domain.secret.KeyPair;
import com.kryptokrauts.aeternity.sdk.exception.AException;
import com.kryptokrauts.aeternity.sdk.service.keypair.Slip0100JsonStruct.DerivedKeyEntry;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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
                HDWallet generatedKeyPair = keyPairService.generateHDWallet(defaultPassword);
                HDWallet restoredKeyPairWithSamePWD =
                    keyPairService.recoverHDWallet(
                        generatedKeyPair.getMnemonicSeedWords(), defaultPassword);
                HDWallet restoredKeyPairWithoutPWD =
                    keyPairService.recoverHDWallet(generatedKeyPair.getMnemonicSeedWords(), null);

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
                                  KeyPairServiceConfiguration.configure()
                                      .entropySizeInByte(2)
                                      .compile());
                      assertThrows(
                          AException.class,
                          () -> {
                            keyPairServiceWrongConfig.generateHDWallet(null);
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

                      HDWallet restoredDefault =
                          keyPairService.recoverHDWallet(mnemonic, defaultPassword);

                      KeyPair restoredMasterKeyPair = restoredDefault.getLastChildKeyPair();

                      Assert.assertEquals(firstChildAddress, restoredMasterKeyPair.getAddress());
                      Assert.assertEquals(
                          firtChildPrivateKeyAsHex, restoredMasterKeyPair.getEncodedPrivateKey());
                    });
                Spectrum.it(
                    "hdKeyPair index test",
                    () -> {
                      List<String> mnemonic =
                          Arrays.asList(
                              "abandon", "abandon", "abandon", "abandon", "abandon", "abandon",
                              "abandon", "abandon", "abandon", "abandon", "abandon", "about");

                      HDWallet restoredDefault =
                          keyPairService.recoverHDWallet(mnemonic, defaultPassword);

                      IntStream.range(0, 10)
                          .forEach(v -> keyPairService.getNextKeyPair(restoredDefault));

                      Assert.assertEquals(10, restoredDefault.getLastChildKeyPair().getIndex());
                    });
                Spectrum.it(
                    "test keys contained in hdwallet",
                    () -> {
                      List<String> mnemonic =
                          Arrays.asList(
                              "acquire useful napkin ranch witness scare lunch smart sibling situate potato inspire"
                                  .split(" "));

                      List<String> addresses =
                          Arrays.asList(
                              "ak_6z3Efre5nVXLB1togCbr2AWrjnKZNWRAXukSdTPPrAAzdFvXC",
                              "ak_256JtFuqyGJ8PPTdkvSi9Lg1E1HhsWeEnxVMUU2tWJQLsUUm3v",
                              "ak_26uXcydJUDHP7cZ3hc8qjqXKyYSGLCEcH3G6Rnqrpk7midqXcD",
                              "ak_hcmRKKNi1V47VxDMBJCbL9yTHJ7LK6HaXLBVdfDmUmaJ6NLkK",
                              "ak_vazGmR7dfFd63S1W9ftZNiJPrwKcgEcEF92NMNvWo6GmZh61q",
                              "ak_2ukHvt54cZKiik1TxV25iXMuaUVM1NyPWRYCipBATSjELJjP59",
                              "ak_2ty5zKb3hHjKZQxRiCEGuqgdi6ZoxpzCyDBNgJw3UNj51rkyMq",
                              "ak_2ruLcwMRLE6gqmZVSvmyTvuJyZVns6G7ALfrNn61R8CGoCM5Ks",
                              "ak_2MBAhS1bb5NZNaEpYPxC7WaHEXDdwPMuhf7nqzycKDNq9t7p67",
                              "ak_29aErGt2GoeypzUG2BbGyDgSVsepLRHxtVo3Jtgte6mq6aHJw3",
                              "ak_2mPuRrdQE2kRLFnwVc9ES6a9kugWHuLLXGa74vY4CbWTCn4PCV");

                      HDWallet restoredDefault = keyPairService.recoverHDWallet(mnemonic, "");

                      for (int i = 0; i < 10; i++) {
                        keyPairService.getNextKeyPair(restoredDefault);
                      }

                      Assert.assertEquals(
                          addresses,
                          restoredDefault.getChildKeyPairs().stream()
                              .map(k -> k.getAddress())
                              .collect(Collectors.toList()));
                    });
                Spectrum.it(
                    "slip0010 test vectors",
                    () -> {
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
                        HDWallet restored =
                            keyPairService.recoverHDWallet(
                                Arrays.asList(testVector.getMnemonic().split(" ")),
                                testVector.getPassword());
                        testVector
                            .getAccounts()
                            .sort(Comparator.comparing(DerivedKeyEntry::getIndex));
                        // List<HDKeyPair> childKeys = restored.getDeterministicHierarchy()
                        for (DerivedKeyEntry derivedKey : testVector.getAccounts()) {
                          KeyPair derivedKeyPair = null;
                          // if index == 0 -> master key
                          if (derivedKey.getIndex() == 0) {
                            derivedKeyPair = restored.getLastChildKeyPair();
                          }
                          // otherwise get the next derived keypair
                          else {
                            derivedKeyPair = keyPairService.getNextKeyPair(restored);
                          }
                          Assert.assertEquals(derivedKey.getAddress(), derivedKeyPair.getAddress());
                          Assert.assertEquals(
                              derivedKey.getPk(), derivedKeyPair.getEncodedPrivateKey());
                        }
                      }
                    });
              });
        });
  }
}
