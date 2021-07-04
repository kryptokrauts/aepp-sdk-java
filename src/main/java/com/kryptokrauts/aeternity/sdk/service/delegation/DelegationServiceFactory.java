package com.kryptokrauts.aeternity.sdk.service.delegation;

import com.kryptokrauts.aeternity.sdk.service.AbstractServiceFactory;
import com.kryptokrauts.aeternity.sdk.service.ServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.delegation.impl.DelegationServiceImpl;

public class DelegationServiceFactory
    extends AbstractServiceFactory<DelegationService, ServiceConfiguration> {

  @Override
  public DelegationService getService() {
    return getService(ServiceConfiguration.configure().compile());
  }

  @Override
  protected DelegationService getServiceWithConfig(ServiceConfiguration config) {
    return new DelegationServiceImpl(config);
  }
}
