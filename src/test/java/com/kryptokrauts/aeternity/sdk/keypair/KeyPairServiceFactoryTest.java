package com.kryptokrauts.aeternity.sdk.keypair;

import org.junit.Assert;

import com.greghaskins.spectrum.Spectrum;
import com.kryptokrauts.aeternity.sdk.BaseTest;
import com.kryptokrauts.aeternity.sdk.service.keypair.KeyPairService;
import com.kryptokrauts.aeternity.sdk.service.keypair.KeyPairServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.keypair.KeyPairServiceFactory;

public class KeyPairServiceFactoryTest extends BaseTest {
    {
        Spectrum.describe( "Address Service Factory", () -> {
            Spectrum.it( " returns same instance for configuration", () -> {
                KeyPairServiceFactory addressServiceFactory = new KeyPairServiceFactory();

                KeyPairServiceConfiguration config = KeyPairServiceConfiguration.builder().cipherAlgorithm( "Blowfish" ).secretKeySpec( "PBKDF2WithHmacSHA1" )
                .build();

                KeyPairService firstGet = addressServiceFactory.getService( config );
                KeyPairService secondGet = addressServiceFactory.getService( config );
                Assert.assertEquals( secondGet, firstGet );
            } );
        } );
    }
}
