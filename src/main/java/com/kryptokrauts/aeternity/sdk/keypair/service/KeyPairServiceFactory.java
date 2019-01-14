package com.kryptokrauts.aeternity.sdk.keypair.service;

import com.kryptokrauts.aeternity.sdk.keypair.KeyPairServiceVersion;
import com.kryptokrauts.aeternity.sdk.keypair.service.impl.KeyPairServiceImpl;
import com.kryptokrauts.aeternity.sdk.service.AbstractServiceFactory;

import static com.kryptokrauts.aeternity.sdk.keypair.KeyPairServiceVersion.LATEST;

public class KeyPairServiceFactory extends AbstractServiceFactory<KeyPairService, KeyPairServiceVersion> {

    @Override
    public KeyPairService getService(KeyPairServiceVersion serviceVersion) {

        KeyPairService instanceToReturn = instanceList.get(serviceVersion);

        if (instanceToReturn == null) {
            switch (serviceVersion) {

                case V_1:
                    instanceToReturn = new KeyPairServiceImpl();
            }
            instanceList.put(serviceVersion, instanceToReturn);
        }
        return instanceToReturn;
    }

    @Override
    public KeyPairService getService() {
        return getService(LATEST);
    }
}
