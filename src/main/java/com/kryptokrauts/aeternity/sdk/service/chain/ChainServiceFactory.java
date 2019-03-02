package com.kryptokrauts.aeternity.sdk.service.chain;

import com.kryptokrauts.aeternity.sdk.service.AbstractServiceFactory;
import com.kryptokrauts.aeternity.sdk.service.ServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.chain.impl.ChainServiceImpl;

public class ChainServiceFactory
    extends AbstractServiceFactory<ChainService, ServiceConfiguration> {
  @Override
  public ChainService getService() {
    return getServiceWithConfig(ServiceConfiguration.configure().compile());
  }

  @Override
  protected ChainService getServiceWithConfig(ServiceConfiguration config) {
    return new ChainServiceImpl(config);
  }
}
