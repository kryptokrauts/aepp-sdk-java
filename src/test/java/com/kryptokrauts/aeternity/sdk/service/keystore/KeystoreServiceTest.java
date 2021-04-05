package com.kryptokrauts.aeternity.sdk.service.keystore;

import com.greghaskins.spectrum.Spectrum;
import com.kryptokrauts.aeternity.sdk.BaseTest;
import com.kryptokrauts.aeternity.sdk.domain.secret.KeyPair;
import com.kryptokrauts.aeternity.sdk.exception.AException;
import com.kryptokrauts.aeternity.sdk.service.keypair.KeyPairService;
import com.kryptokrauts.aeternity.sdk.service.keypair.KeyPairServiceFactory;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Assertions;

public class KeystoreServiceTest extends BaseTest {
  {
    final KeyPairService keypairService = new KeyPairServiceFactory().getService();
    final KeystoreService walletService = new KeystoreServiceFactory().getService();

    Spectrum.describe(
        "wallet service tests",
        () -> {
          Spectrum.it(
              "keyPair can be recovered from a generated wallet",
              () -> {
                final String walletFileSecret = "my_super_safe_password";

                // generate Keypair
                KeyPair keypair = keypairService.generateKeyPair();
                String json = walletService.createKeystore(keypair, walletFileSecret, null);
                Assertions.assertNotNull(json);

                // recover Keypair
                byte[] recoveredPrivateKey =
                    walletService.recoverPrivateKeyFromKeystore(json, walletFileSecret);
                KeyPair recoveredRawKeypair =
                    keypairService.recoverKeyPair(Hex.toHexString(recoveredPrivateKey));
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
                byte[] privateKey =
                    walletService.recoverPrivateKeyFromKeystore(keystore, walletFileSecret);
                KeyPair keyPair = keypairService.recoverKeyPair(Hex.toHexString(privateKey));
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
                  walletService.recoverPrivateKeyFromKeystore(keystore, walletFileSecret);
                  Assertions.fail();
                } catch (AException e) {
                  Assertions.assertEquals(
                      "Error recovering privateKey: wrong password.", e.getMessage());
                }
              });
        });
  }
}
