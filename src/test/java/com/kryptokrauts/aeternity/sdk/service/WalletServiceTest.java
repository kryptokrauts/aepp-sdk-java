package com.kryptokrauts.aeternity.sdk.service;

import org.junit.Assert;
import org.spongycastle.util.encoders.Hex;

import com.greghaskins.spectrum.Spectrum;
import com.kryptokrauts.aeternity.sdk.AEKit;
import com.kryptokrauts.aeternity.sdk.BaseTest;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.RawKeyPair;

public class WalletServiceTest extends BaseTest
{
    {
        Spectrum.describe( "wallet service tests", () -> {
            Spectrum.it( "test building of test wallet file", () -> {
                final String walletFileSecret = "my_super_safe_password";

                // generate Keypair
                RawKeyPair keypair = AEKit.getKeyPairService().generateRawKeyPair();
                String json = AEKit.getWalletService().generateWalletFile( keypair, walletFileSecret, null );
                Assert.assertNotNull( json );
                System.out.println( "Generated keystore file\n" + json );
                // prints
                System.out.println( "Raw private: " + Hex.toHexString( keypair.getPrivateKey() ) );
                System.out.println( "Raw public: " + Hex.toHexString( keypair.getPublicKey() ) );
                System.out.println( "Wallet: " + AEKit.getWalletService().getWalletAddress( keypair ) );

                // recover Keypair
                byte[] recoveredPrivateKey = AEKit.getWalletService().recoverPrivateKeyFromWalletFile( json, walletFileSecret );
                RawKeyPair recoveredRawKeypair = AEKit.getKeyPairService().generateRawKeyPairFromSecret( Hex.toHexString( recoveredPrivateKey ) );
                Assert.assertNotNull( recoveredRawKeypair );

                // compare generated and recovered keypair
                Assert.assertEquals( keypair, recoveredRawKeypair );
            } );
        } );
    }

}
