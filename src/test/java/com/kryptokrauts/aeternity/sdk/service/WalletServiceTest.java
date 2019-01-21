package com.kryptokrauts.aeternity.sdk.service;

import com.greghaskins.spectrum.Spectrum;
import com.kryptokrauts.aeternity.sdk.AEKit;
import com.kryptokrauts.aeternity.sdk.BaseTest;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.BaseKeyPair;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.RawKeyPair;
import com.kryptokrauts.aeternity.sdk.exception.AException;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Assertions;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class WalletServiceTest extends BaseTest
{
    {
        Spectrum.describe( "wallet service tests", () -> {
            Spectrum.it( "keyPair can be recovered from a generated wallet", () -> {
                final String walletFileSecret = "my_super_safe_password";

                // generate Keypair
                RawKeyPair keypair = AEKit.getKeyPairService().generateRawKeyPair();
                String json = AEKit.getWalletService().generateWalletFile( keypair, walletFileSecret, null );
                Assertions.assertNotNull( json );

                // recover Keypair
                byte[] recoveredPrivateKey = AEKit.getWalletService().recoverPrivateKeyFromWalletFile( json, walletFileSecret );
                RawKeyPair recoveredRawKeypair = AEKit.getKeyPairService().generateRawKeyPairFromSecret( Hex.toHexString( recoveredPrivateKey ) );
                Assertions.assertNotNull( recoveredRawKeypair );

                // compare generated and recovered keypair
                Assertions.assertEquals( keypair, recoveredRawKeypair );
            } );
            Spectrum.it( "recovery of a valid keystore.json works", () -> {
                final String walletFileSecret = "aeternity";
                final String expectedPubKey = "ak_2hSFmdK98bhUw4ar7MUdTRzNQuMJfBFQYxdhN9kaiopDGqj3Cr";
                final InputStream inputStream =
                        Thread.currentThread().getContextClassLoader().getResourceAsStream("keystore.json");
                String keystore = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                byte[] privateKey = AEKit.getWalletService().recoverPrivateKeyFromWalletFile(keystore, walletFileSecret);
                BaseKeyPair keyPair = AEKit.getKeyPairService().generateBaseKeyPairFromSecret(Hex.toHexString(privateKey));
                Assertions.assertEquals(expectedPubKey, keyPair.getPublicKey());
            } );
            Spectrum.it( "recovery of a valid keystore.json fails with wrong password", () -> {
                final String walletFileSecret = "abc";
                final String expectedPubKey = "ak_2hSFmdK98bhUw4ar7MUdTRzNQuMJfBFQYxdhN9kaiopDGqj3Cr";
                final InputStream inputStream =
                        Thread.currentThread().getContextClassLoader().getResourceAsStream("keystore.json");
                String keystore = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                try {
                    AEKit.getWalletService().recoverPrivateKeyFromWalletFile(keystore, walletFileSecret);
                    Assertions.fail();
                } catch ( AException e ) {
                    Assertions.assertEquals("Error recovering privateKey: wrong password.", e.getMessage());
                }
            } );
        } );
    }

}
