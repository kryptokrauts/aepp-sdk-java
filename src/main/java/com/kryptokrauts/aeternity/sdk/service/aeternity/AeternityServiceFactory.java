package com.kryptokrauts.aeternity.sdk.service.aeternity;

import com.kryptokrauts.aeternity.sdk.service.AbstractServiceFactory;
import com.kryptokrauts.aeternity.sdk.service.aeternity.impl.AeternityService;

public class AeternityServiceFactory
    extends AbstractServiceFactory<AeternityService, AeternityServiceConfiguration> {

  @Override
  public AeternityService getService() {
    return getServiceWithConfig(AeternityServiceConfiguration.configure().compile());
  }

  @Override
  protected AeternityService getServiceWithConfig(AeternityServiceConfiguration config) {
    return new AeternityService(config);
  }
}
