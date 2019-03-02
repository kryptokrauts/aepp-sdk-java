package com.kryptokrauts.aeternity.sdk.service.account;

import com.kryptokrauts.aeternity.sdk.service.AbstractServiceFactory;
import com.kryptokrauts.aeternity.sdk.service.ServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.account.impl.AccountServiceImpl;

public class AccountServiceFactory
    extends AbstractServiceFactory<AccountService, ServiceConfiguration> {

  @Override
  public AccountService getService() {
    return getService(ServiceConfiguration.configure().compile());
  }

  @Override
  protected AccountService getServiceWithConfig(ServiceConfiguration config) {
    return new AccountServiceImpl(config);
  }
}
