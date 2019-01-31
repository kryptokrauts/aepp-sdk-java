package com.kryptokrauts.aeternity.sdk.service.transaction;

import com.kryptokrauts.aeternity.sdk.service.AbstractServiceFactory;
import com.kryptokrauts.aeternity.sdk.service.transaction.impl.TransactionServiceImpl;

public class TransactionServiceFactory extends AbstractServiceFactory<TransactionService, TransactionServiceConfiguration> {
    @Override
    public TransactionService getService() {
        return getServiceWithConfig( TransactionServiceConfiguration.configure().compile() );
    }

    @Override
    public TransactionService getServiceWithConfig( TransactionServiceConfiguration config ) {
        return new TransactionServiceImpl( config );
    }
}
