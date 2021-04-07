package com.kryptokrauts.aeternity.sdk.service.keystore;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greghaskins.spectrum.Spectrum;
import com.kryptokrauts.aeternity.sdk.BaseTest;
import com.kryptokrauts.aeternity.sdk.domain.Keystore;
import com.kryptokrauts.aeternity.sdk.domain.secret.HDWallet;
import com.kryptokrauts.aeternity.sdk.domain.secret.KeyPair;
import com.kryptokrauts.aeternity.sdk.exception.AException;
import com.kryptokrauts.aeternity.sdk.service.keypair.KeyPairService;
import com.kryptokrauts.aeternity.sdk.service.keypair.KeyPairServiceFactory;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;

public class KeystoreServiceTest extends BaseTest {
  {
    final KeyPairService keypairService = new KeyPairServiceFactory().getService();
    final KeystoreService keystoreService = new KeystoreServiceFactory().getService();

    Spectrum.describe(
        "wallet service tests",
        () -> {
          Spectrum.it(
              "keyPair can be recovered from a generated wallet",
              () -> {
                final String keystoreFileSecret = "my_super_safe_password";

                // generate Keypair
                KeyPair keypair = keypairService.generateKeyPair();
                String json = keystoreService.createKeystore(keypair, keystoreFileSecret, null);
                Assertions.assertNotNull(json);

                // recover Keypair
                String recoveredPrivateKey =
                    keystoreService.recoverEncodedPrivateKey(json, keystoreFileSecret);
                KeyPair recoveredRawKeypair = keypairService.recoverKeyPair(recoveredPrivateKey);
                Assertions.assertNotNull(recoveredRawKeypair);

                // compare generated and recovered keypair
                Assertions.assertEquals(keypair, recoveredRawKeypair);
              });
          Spectrum.it(
              "recovery of a valid keystore.json works",
              () -> {
                final String walletFileSecret = "aeternity";
                final String expectedPubKey =
                    "ak_2hSFmdK98bhUw4ar7MUdTRzNQuMJfBFQYxdhN9kaiopDGqj3Cr";
                final InputStream inputStream =
                    Thread.currentThread()
                        .getContextClassLoader()
                        .getResourceAsStream("keystore.json");
                String keystore = IOUtils.toString(inputStream, StandardCharsets.UTF_8.toString());
                String privateKey =
                    keystoreService.recoverEncodedPrivateKey(keystore, walletFileSecret);
                KeyPair keyPair = keypairService.recoverKeyPair(privateKey);
                Assertions.assertEquals(expectedPubKey, keyPair.getAddress());
              });
          Spectrum.it(
              "recovery of a valid keystore.json fails with wrong password",
              () -> {
                final String walletFileSecret = "abc";
                final InputStream inputStream =
                    Thread.currentThread()
                        .getContextClassLoader()
                        .getResourceAsStream("keystore.json");
                String keystore = IOUtils.toString(inputStream, StandardCharsets.UTF_8.toString());
                try {
                  keystoreService.recoverEncodedPrivateKey(keystore, walletFileSecret);
                  Assertions.fail();
                } catch (AException e) {
                  Assertions.assertEquals(
                      "Error recovering keystore file - wrong password", e.getMessage());
                }
              });
          Spectrum.it(
              "recovery of hdWallet",
              () -> {
                final String keystoreFileSecret = "hd_wallet_password";

                // generate random HDWallet
                HDWallet hdWallet = keypairService.generateHDWallet(null);
                List<String> seedWordsToBeRecovered = hdWallet.getMnemonicSeedWords();
                String json = keystoreService.createKeystore(hdWallet, keystoreFileSecret);
                Assertions.assertNotNull(json);

                // recover seed words
                List<String> recoveredSeedWords =
                    keystoreService.recoverMnemonicSeedWords(json, keystoreFileSecret);
                Assertions.assertNotNull(recoveredSeedWords);

                // compare generated and recovered seed words
                Assertions.assertEquals(seedWordsToBeRecovered, recoveredSeedWords);
              });
          Spectrum.it(
              "recovery of hdWallet fail wrong type",
              () -> {
                final String keystoreFileSecret = "does_not_matter";

                // generate random HDWallet
                HDWallet hdWallet = keypairService.generateHDWallet(null);
                String json = keystoreService.createKeystore(hdWallet, keystoreFileSecret);
                Keystore recoverWallet = new ObjectMapper().readValue(json, Keystore.class);
                recoverWallet.setName("no_hd_wallet_keystore_file");
                json =
                    new ObjectMapper()
                        .writerWithDefaultPrettyPrinter()
                        .writeValueAsString(recoverWallet);
                Assertions.assertNotNull(json);

                // recover seed words
                try {
                  keystoreService.recoverMnemonicSeedWords(json, keystoreFileSecret);
                  Assertions.fail();
                } catch (AException e) {
                  Assertions.assertEquals("Given JSON is not a HDWallet keystore", e.getMessage());
                }
              });
        });
  }
}
