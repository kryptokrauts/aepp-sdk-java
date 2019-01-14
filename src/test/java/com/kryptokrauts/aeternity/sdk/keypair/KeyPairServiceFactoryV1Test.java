package com.kryptokrauts.aeternity.sdk.keypair;

import com.greghaskins.spectrum.Spectrum;
import com.kryptokrauts.aeternity.sdk.BaseTest;
import com.kryptokrauts.aeternity.sdk.keypair.service.KeyPairService;
import com.kryptokrauts.aeternity.sdk.keypair.service.KeyPairServiceFactory;
import org.junit.Assert;

public class KeyPairServiceFactoryV1Test extends BaseTest {
    {
        Spectrum.describe("Address Service Factory", () -> {
            Spectrum.it(" returns same instance for latest version", () ->
            {
                KeyPairServiceFactory addressServiceFactory = new KeyPairServiceFactory();

                KeyPairService latestInstance = addressServiceFactory.getService();
                KeyPairService definedInstance = addressServiceFactory.getService(KeyPairServiceVersion.LATEST);
                Assert.assertEquals(latestInstance, definedInstance);
            });
        });
    }
}
