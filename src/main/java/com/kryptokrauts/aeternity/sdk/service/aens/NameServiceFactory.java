package com.kryptokrauts.aeternity.sdk.service.aens;

import com.kryptokrauts.aeternity.sdk.service.AbstractServiceFactory;
import com.kryptokrauts.aeternity.sdk.service.ServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.aens.impl.NameServiceImpl;

public class NameServiceFactory extends AbstractServiceFactory<NameService, ServiceConfiguration> {

  @Override
  public NameService getService() {
    return getService(ServiceConfiguration.configure().compile());
  }

  @Override
  protected NameService getServiceWithConfig(ServiceConfiguration config) {
    return new NameServiceImpl(config);
  }
}
