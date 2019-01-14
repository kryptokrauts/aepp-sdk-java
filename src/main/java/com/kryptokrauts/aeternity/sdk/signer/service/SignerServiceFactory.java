package com.kryptokrauts.aeternity.sdk.signer.service;

import com.kryptokrauts.aeternity.sdk.service.AbstractServiceFactory;
import com.kryptokrauts.aeternity.sdk.signer.SignerServiceVersion;
import com.kryptokrauts.aeternity.sdk.signer.service.impl.SignerServiceImpl;

import static com.kryptokrauts.aeternity.sdk.signer.SignerServiceVersion.LATEST;


public class SignerServiceFactory extends AbstractServiceFactory<SignerService, SignerServiceVersion> {

    @Override
    public SignerService getService(SignerServiceVersion serviceVersion) {

        SignerService instanceToReturn = instanceList.get(serviceVersion);

        if (instanceToReturn == null) {
            switch (serviceVersion) {

                case V_1:
                    instanceToReturn = new SignerServiceImpl();
            }
            instanceList.put(serviceVersion, instanceToReturn);
        }
        return instanceToReturn;
    }

    @Override
    public SignerService getService() {
        return getService(LATEST);
    }
}
