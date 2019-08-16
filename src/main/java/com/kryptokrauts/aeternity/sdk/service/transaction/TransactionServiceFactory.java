package com.kryptokrauts.aeternity.sdk.service.transaction;

import com.kryptokrauts.aeternity.sdk.service.AbstractServiceFactory;
import com.kryptokrauts.aeternity.sdk.service.aeternity.AeternityServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.transaction.impl.TransactionServiceImpl;

public class TransactionServiceFactory
		extends AbstractServiceFactory<TransactionService, AeternityServiceConfiguration> {
	@Override
	public TransactionService getService() {
		return getServiceWithConfig(TransactionServiceConfiguration.configure().compile());
	}

	@Override
	protected TransactionService getServiceWithConfig(AeternityServiceConfiguration config) {
		return new TransactionServiceImpl(config);
	}
}
