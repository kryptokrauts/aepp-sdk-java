package com.kryptokrauts.aeternity.sdk.service.aeternity.impl;

import com.kryptokrauts.aeternity.generated.api.ExternalApiImpl;
import com.kryptokrauts.aeternity.generated.api.InternalApiImpl;
import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.api.rxjava.InternalApi;
import com.kryptokrauts.aeternity.sdk.service.account.AccountService;
import com.kryptokrauts.aeternity.sdk.service.account.impl.AccountServiceImpl;
import com.kryptokrauts.aeternity.sdk.service.aeternity.AeternityServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.compiler.CompilerService;
import com.kryptokrauts.aeternity.sdk.service.compiler.impl.SophiaCompilerServiceImpl;
import com.kryptokrauts.aeternity.sdk.service.indaex.IndaexService;
import com.kryptokrauts.aeternity.sdk.service.indaex.impl.IndaexServiceImpl;
import com.kryptokrauts.aeternity.sdk.service.info.InfoService;
import com.kryptokrauts.aeternity.sdk.service.info.impl.InfoServiceImpl;
import com.kryptokrauts.aeternity.sdk.service.name.NameService;
import com.kryptokrauts.aeternity.sdk.service.name.impl.NameServiceImpl;
import com.kryptokrauts.aeternity.sdk.service.oracle.OracleService;
import com.kryptokrauts.aeternity.sdk.service.oracle.impl.OracleServiceImpl;
import com.kryptokrauts.aeternity.sdk.service.transaction.TransactionService;
import com.kryptokrauts.aeternity.sdk.service.transaction.impl.TransactionServiceImpl;
import com.kryptokrauts.sophia.compiler.generated.api.DefaultApiImpl;
import com.kryptokrauts.sophia.compiler.generated.api.rxjava.DefaultApi;
import lombok.NonNull;

/**
 * the central service to access all provided functions. these are classified into their purpose for
 * better organization
 */
public class AeternityService {

  @NonNull protected AeternityServiceConfiguration config;

  private ExternalApi externalApi;

  private InternalApi internalApi;

  private DefaultApi compilerApi;

  private com.kryptokrauts.indaex.generated.api.rxjava.MiddlewareApi indaexApi;

  public TransactionService transactions;

  public AccountService accounts;

  public IndaexService indaex;

  public CompilerService compiler;

  public InfoService info;

  public NameService names;

  public OracleService oracles;

  public AeternityService(AeternityServiceConfiguration config) {
    this.config = config;
    this.externalApi = new ExternalApi(new ExternalApiImpl(config.getApiClient()));
    this.internalApi = new InternalApi(new InternalApiImpl(config.getApiClient()));
    this.compilerApi = new DefaultApi(new DefaultApiImpl(config.getCompilerApiClient()));
    this.indaexApi =
        new com.kryptokrauts.indaex.generated.api.rxjava.MiddlewareApi(
            new com.kryptokrauts.indaex.generated.api.MiddlewareApiImpl(
                config.getIndaexApiClient()));
    this.accounts = new AccountServiceImpl(this.config, this.externalApi);
    this.compiler = new SophiaCompilerServiceImpl(this.config, this.compilerApi);
    this.indaex = new IndaexServiceImpl(this.indaexApi);
    this.info = new InfoServiceImpl(this.config, this.externalApi);
    this.names = new NameServiceImpl(this.config, this.externalApi);
    this.oracles = new OracleServiceImpl(this.config, this.externalApi);
    this.transactions =
        new TransactionServiceImpl(
            this.config, this.externalApi, this.internalApi, this.compilerApi, this.info);
  }
}
