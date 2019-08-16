package com.kryptokrauts.aeternity.sdk.service.aeternity.impl;

import com.kryptokrauts.aeternity.generated.api.ExternalApiImpl;
import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.sdk.service.account.AccountService;
import com.kryptokrauts.aeternity.sdk.service.account.impl.AccountServiceImpl;
import com.kryptokrauts.aeternity.sdk.service.aeternity.AeternityServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.transaction.TransactionService;
import com.kryptokrauts.aeternity.sdk.service.transaction.impl.TransactionServiceImpl;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.TransactionFactory;
import com.kryptokrauts.sophia.compiler.generated.api.DefaultApiImpl;
import com.kryptokrauts.sophia.compiler.generated.api.rxjava.DefaultApi;

import lombok.NonNull;

public class AeternityService {

	@NonNull
	protected AeternityServiceConfiguration config;

	private ExternalApi externalApi;

	private DefaultApi compilerApi;

	private TransactionFactory transactionFactory;

	public TransactionService transactions;

	public AccountService accounts;

	public AeternityService(AeternityServiceConfiguration config) {
		this.config = config;
		this.externalApi = new ExternalApi(new ExternalApiImpl(config.getApiClient()));
		this.compilerApi = new DefaultApi(new DefaultApiImpl(config.getCompilerApiClient()));
		transactionFactory = new TransactionFactory(this.externalApi);
		transactions = new TransactionServiceImpl(this.config, this.externalApi);
		accounts = new AccountServiceImpl(this.config, this.externalApi);
	}

//	public TransactionService transactions() {
//		return transactionService;
//	}

	public TransactionFactory models() {
		return this.transactionFactory;
	}
}
