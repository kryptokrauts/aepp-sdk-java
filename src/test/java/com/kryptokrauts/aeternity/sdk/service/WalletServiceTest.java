package com.kryptokrauts.aeternity.sdk.service;

import com.kryptokrauts.aeternity.sdk.BaseTest;


public class WalletServiceTest extends BaseTest {
    /*{
        Spectrum.describe("wallet service tests", () -> {
            Spectrum.it("test building of test wallet file", () -> {
                Keystore testWalletObject = Keystore.builder()
                                                    .publicKey("ak_2hSFmdK98bhUw4ar7MUdTRzNQuMJfBFQYxdhN9kaiopDGqj3Cr")
                                                    .crypto(Keystore.Crypto.builder().secretType("ed25519")
                                                                           .symmetricAlgorithm("xsalsa20-poly1305")
                                                                           .cipherText("71acf8412331806b3ad5482cda1f6e682c541c522f61715056faf5ed2d21a9c4d68fe2cdcf147b1be99fbab20b33433f82b2a2d3bbc772957bd2fb6cf9a97f611670e0f044d8076efbe31fe30142e6f5")
                                                                           .cipherParams(Keystore.CipherParams.builder()
                                                                                                              .nonce("375673a887fd10910fe3bfa9c9abfd72d3d240d124ed3f3b")
                                                                                                              .build())
                                                                           .kdf("argon2id")
                                                                           .kdfParams(Keystore.KdfParams.builder()
                                                                                                        .memLimitKib(65536)
                                                                                                        .opsLimit(2)
                                                                                                        .salt("a84887d9539cbd9490d2b1a5c41262b2")
                                                                                                        .parallelism(1)
                                                                                                        .build())
                                                                           .build())
                                                    .id("ff7d9f9c-e0ab-4fab-b8fc-beab2d322f6b")
                                                    .name("!!!FOR TESTING PURPOSE ONLY!!! - Wed 14 Nov 2018 09:40:10 CET - password:aeternity")
                                                    .version(1).build();
                File file = new File(this.getClass().getClassLoader().getResource("keystore.json").getFile());
                Assert.assertNotNull(file);
                String awaitedJSON = String
                        .join("", Files
                                .readAllLines(Paths.get(file.toURI())));

                Assert.assertEquals(awaitedJSON.trim().replace(" ", ""), AEKit.getWalletService()
                                                                              .generateWalletJSON(testWalletObject)
                                                                              .trim().replace(" ", ""));
            });
        });
    }*/


}
