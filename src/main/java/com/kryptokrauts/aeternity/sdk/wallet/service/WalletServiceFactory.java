package com.kryptokrauts.aeternity.sdk.wallet.service;

import com.kryptokrauts.aeternity.sdk.service.AbstractServiceFactory;
import com.kryptokrauts.aeternity.sdk.wallet.WalletServiceVersion;
import com.kryptokrauts.aeternity.sdk.wallet.service.impl.WalletServiceImpl;

import static com.kryptokrauts.aeternity.sdk.wallet.WalletServiceVersion.LATEST;

public class WalletServiceFactory extends AbstractServiceFactory<WalletService, WalletServiceVersion> {

    @Override
    public WalletService getService(WalletServiceVersion serviceVersion) {

        WalletService instanceToReturn = instanceList.get(serviceVersion);

        if (instanceToReturn == null) {
            switch (serviceVersion) {

                case V_1:
                    instanceToReturn = new WalletServiceImpl();
            }
            instanceList.put(serviceVersion, instanceToReturn);
        }
        return instanceToReturn;
    }

    @Override
    public WalletService getService() {
        return getService(LATEST);
    }
}
