package com.kryptokrauts.aeternity.sdk.service.keypair;

import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Assert;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.greghaskins.spectrum.Spectrum;
import com.kryptokrauts.aeternity.sdk.BaseTest;
import com.kryptokrauts.aeternity.sdk.domain.secret.HDWallet;
import com.kryptokrauts.aeternity.sdk.domain.secret.KeyPair;
import com.kryptokrauts.aeternity.sdk.exception.AException;
import com.kryptokrauts.aeternity.sdk.service.keypair.Slip0100JsonStruct.DerivedKeyEntry;

public class KeyPairServiceTest extends BaseTest {
  {
    Spectrum.describe("Keypair Service Test", () -> {
      Spectrum.describe("Mnemonic keypair generation tests", () -> {
        KeyPairService keyPairService = new KeyPairServiceFactory().getService();
        HDWallet generatedKeyPair = keyPairService.generateMasterMnemonicKeyPair(defaultPassword);
        HDWallet restoredKeyPairWithSamePWD = keyPairService
            .recoverMasterMnemonicKeyPair(generatedKeyPair.getMnemonicSeedWords(), defaultPassword);
        HDWallet restoredKeyPairWithoutPWD = keyPairService
            .recoverMasterMnemonicKeyPair(generatedKeyPair.getMnemonicSeedWords(), null);

        Spectrum.it("mnemonic keypair recovered from word seed list is same", () -> {
          Assert.assertEquals(
              Hex.toHexString(generatedKeyPair.getMasterKeyPair().getRawPrivateKey()),
              Hex.toHexString(restoredKeyPairWithSamePWD.getMasterKeyPair().getRawPrivateKey()));
        });
        Spectrum.it("mnemonic keypair recovered from word seed list without password is not same",
            () -> {
              Assert.assertNotEquals(
                  Hex.toHexString(generatedKeyPair.getMasterKeyPair().getRawPrivateKey()),
                  Hex.toHexString(restoredKeyPairWithoutPWD.getMasterKeyPair().getRawPrivateKey()));
            });
        Spectrum.it("mnemonic keypair cannot be generated due to small entropy", () -> {
          KeyPairService keyPairServiceWrongConfig = new KeyPairServiceFactory()
              .getService(KeyPairServiceConfiguration.configure().entropySizeInByte(2).compile());
          assertThrows(AException.class, () -> {
            keyPairServiceWrongConfig.generateMasterMnemonicKeyPair(null);
          });
        });
        Spectrum.it("default vector recover test", () -> {
          List<String> mnemonic = Arrays.asList("abandon", "abandon", "abandon", "abandon",
              "abandon", "abandon", "abandon", "abandon", "abandon", "abandon", "abandon", "about");

          String firtChildPrivateKeyAsHex =
              "cb9b31d8e09182ab7228a3252984a68c41513cea33283313680f34c382c58127769cd5368a1a498dff86d3aec54ff4eb3ac89c13d6b100461907133bfa5cd082";
          String firstChildAddress = "ak_uEoRY2CEAbSzMbN4ohniNHFdrVDHjpw3Za3upmJA8SihGBjCk";

          HDWallet restoredDefault =
              keyPairService.recoverMasterMnemonicKeyPair(mnemonic, defaultPassword);

          KeyPair restoredKeyPair = restoredDefault.getLastChild();

          Assert.assertEquals(firstChildAddress, restoredKeyPair.getAddress());
          Assert.assertEquals(firtChildPrivateKeyAsHex, restoredKeyPair.getEncodedPrivateKey());
        });
        Spectrum.it("test child index at fail", () -> {
          List<String> mnemonic = Arrays.asList("abandon", "abandon", "abandon", "abandon",
              "abandon", "abandon", "abandon", "abandon", "abandon", "abandon", "abandon", "about");

          HDWallet restoredDefault =
              keyPairService.recoverMasterMnemonicKeyPair(mnemonic, defaultPassword);

          int missingKeyPairAtIndex = 42;

          assertThrows(AException.class, () -> {
            restoredDefault.getChildAt(missingKeyPairAtIndex);
          }, "Cannot retrieve child at index " + missingKeyPairAtIndex
              + " - no child keypair was generated for this index. Max child index available: 0");
        });
        Spectrum.it("slip0010 test vectors", () -> {
          List<Slip0100JsonStruct> slip0100Resources = new ObjectMapper().readValue(
              this.getClass().getClassLoader().getResourceAsStream("derivedkeys.json"),
              new TypeReference<List<Slip0100JsonStruct>>() {});
          System.out.println("Generating keys for " + slip0100Resources.size() + " test vectors");
          for (Slip0100JsonStruct testVector : slip0100Resources) {
            HDWallet restored = keyPairService.recoverMasterMnemonicKeyPair(
                Arrays.asList(testVector.getMnemonic().split(" ")), testVector.getPassword());
            testVector.getAccounts().sort(Comparator.comparing(DerivedKeyEntry::getIndex));
            for (DerivedKeyEntry derivedKey : testVector.getAccounts()) {
              KeyPair derivedKeyPair = null;
              if (derivedKey.getIndex() == 0) {
                derivedKeyPair = restored.getChildAt(derivedKey.getIndex());
              } else {
                derivedKeyPair = keyPairService.deriveNextKeyPair(restored);
              }
              Assert.assertEquals(derivedKey.getAddress(), derivedKeyPair.getAddress());
              Assert.assertEquals(derivedKey.getPk(), derivedKeyPair.getEncodedPrivateKey());
            }
          }
        });
      });
    });
  }
}
