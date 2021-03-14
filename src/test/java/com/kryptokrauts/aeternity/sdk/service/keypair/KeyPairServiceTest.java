package com.kryptokrauts.aeternity.sdk.service.keypair;

import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.Arrays;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Assert;
import com.greghaskins.spectrum.Spectrum;
import com.kryptokrauts.aeternity.sdk.BaseTest;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.Account;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.MnemonicKeyPair;
import com.kryptokrauts.aeternity.sdk.exception.AException;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;

public class KeyPairServiceTest extends BaseTest {
  {
    Spectrum.describe("Keypair Service Test", () -> {
      Spectrum.describe("Mnemonic keypair generation tests", () -> {
        KeyPairService keyPairService = new KeyPairServiceFactory().getService();
        MnemonicKeyPair generatedKeyPair =
            keyPairService.generateMasterMnemonicKeyPair(defaultPassword);
        MnemonicKeyPair restoredKeyPairWithSamePWD = keyPairService
            .recoverMasterMnemonicKeyPair(generatedKeyPair.getMnemonicSeedWords(), defaultPassword);
        MnemonicKeyPair restoredKeyPairWithoutPWD = keyPairService
            .recoverMasterMnemonicKeyPair(generatedKeyPair.getMnemonicSeedWords(), null);

        Spectrum.it("mnemonic keypair recovered from word seed list is same", () -> {
          Assert.assertEquals(Hex.toHexString(generatedKeyPair.getPrivateKey()),
              Hex.toHexString(restoredKeyPairWithSamePWD.getPrivateKey()));
        });
        Spectrum.it("mnemonic keypair recovered from word seed list without password is not same",
            () -> {
              Assert.assertNotEquals(Hex.toHexString(generatedKeyPair.getPrivateKey()),
                  Hex.toHexString(restoredKeyPairWithoutPWD.getPrivateKey()));
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

          MnemonicKeyPair restoredDefault =
              keyPairService.recoverMasterMnemonicKeyPair(mnemonic, defaultPassword);

          Account restoredAccount = keyPairService.generateBaseKeyPairFromSecret(
              Hex.toHexString(restoredDefault.getChildAt(0).getPrivateKey()));

          Assert.assertEquals(firstChildAddress, restoredAccount.getAddress());
          Assert.assertEquals(firtChildPrivateKeyAsHex, restoredAccount.getPrivateKey());
        });
        Spectrum.it("hd derivation keys restore test", () -> {
          List<String> mnemonic = Arrays.asList("legal", "winner", "thank", "year", "wave",
              "sausage", "worth", "useful", "legal", "winner", "thank", "yellow");

          MnemonicKeyPair master =
              keyPairService.recoverMasterMnemonicKeyPair(mnemonic, defaultPassword);

          MnemonicKeyPair masterNoPWD = keyPairService.recoverMasterMnemonicKeyPair(mnemonic, "");

          ResourceBundle derivedKeys = ResourceBundle.getBundle("derivedKeys");

          /**
           * make sure every that derived keys can be restored and that the hardened keys differ
           */
          for (int i = 0; i < 20; i++) {
            // derive different keys
            Account generatedDerivedKey = EncodingUtils
                .createAccount(keyPairService.deriveNextAddress(master).toRawKeyPair());
            // BaseKeyPair notHardendedKey = EncodingUtils
            // .createBaseKeyPair(keyPairService.generateDerivedKey(master,
            // false).toRawKeyPair());
            Account generatedDerivedKeyNoPwd = EncodingUtils
                .createAccount(keyPairService.deriveNextAddress(masterNoPWD).toRawKeyPair());
            // BaseKeyPair generatedDerivedKeyWithCustomPath =
            // EncodingUtils.createBaseKeyPair(keyPairService.deriveNextKey(master,
            // true,
            // new ChildNumber(4711, true), new ChildNumber(4712,
            // true)).toRawKeyPair());
            // assert that the generated keys are the same
            Assert.assertEquals(derivedKeys.getObject(generatedDerivedKey.getAddress()),
                generatedDerivedKey.getPrivateKey());

            // make sure, not hardended keys differ
            // assertThrows(MissingResourceException.class, () -> {
            // derivedKeys.getObject(notHardendedKey.getPublicKey());
            // });
            // make sure, keys derived from master with same mnemonics but different pwd
            // are different
            assertThrows(MissingResourceException.class, () -> {
              derivedKeys.getObject(generatedDerivedKeyNoPwd.getAddress());
            });
            // make sure, keys from other derivation path differ
            // assertThrows(MissingResourceException.class, () -> {
            // derivedKeys.getObject(generatedDerivedKeyWithCustomPath.getPublicKey());
            // });
          }
        });
        Spectrum.it("hd derivation keys not possible from derived key test", () -> {
          List<String> mnemonic = Arrays.asList("letter", "advice", "cage", "absurd", "amount",
              "doctor", "acoustic", "avoid", "letter", "advice", "cage", "above");

          MnemonicKeyPair master =
              keyPairService.recoverMasterMnemonicKeyPair(mnemonic, defaultPassword);
          MnemonicKeyPair generatedDerivedKey = keyPairService.deriveNextAddress(master);

          Throwable exception = assertThrows(AException.class, () -> {
            keyPairService.deriveNextAddress(generatedDerivedKey);
          });
          Assert.assertTrue(exception.getMessage()
              .contains("Given mnemonicKeyPair object does not contain the master key"));
        });
      });
    });
  }
}
