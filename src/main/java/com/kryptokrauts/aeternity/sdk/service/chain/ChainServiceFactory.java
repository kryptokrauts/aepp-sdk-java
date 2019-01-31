package com.kryptokrauts.aeternity.sdk.service.chain;

import com.kryptokrauts.aeternity.sdk.service.AbstractServiceFactory;
import com.kryptokrauts.aeternity.sdk.service.chain.impl.ChainServiceImpl;

public class ChainServiceFactory extends AbstractServiceFactory<ChainService, ChainServiceConfiguration> {
    @Override
    public ChainService getService() {
        return getServiceWithConfig( ChainServiceConfiguration.configure().compile() );
    }

    @Override
    public ChainService getServiceWithConfig( ChainServiceConfiguration config ) {
        return new ChainServiceImpl( config );
    }
}
