package com.kryptokrauts.aeternity.sdk.service.keypair;

import com.kryptokrauts.aeternity.sdk.service.AbstractServiceFactory;
import com.kryptokrauts.aeternity.sdk.service.keypair.impl.KeyPairServiceImpl;

public class KeyPairServiceFactory extends AbstractServiceFactory<KeyPairService, KeyPairServiceConfiguration> {
    @Override
    public KeyPairService getService() {
        return getService( KeyPairServiceConfiguration.builder().build() );
    }

    @Override
    protected KeyPairService getServiceWithConfig( KeyPairServiceConfiguration config ) {
        return new KeyPairServiceImpl( config );
    }
}
