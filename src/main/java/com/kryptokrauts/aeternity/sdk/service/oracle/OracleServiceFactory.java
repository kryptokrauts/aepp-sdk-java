package com.kryptokrauts.aeternity.sdk.service.oracle;

import com.kryptokrauts.aeternity.sdk.service.AbstractServiceFactory;
import com.kryptokrauts.aeternity.sdk.service.ServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.oracle.impl.OracleServiceImpl;

public class OracleServiceFactory
    extends AbstractServiceFactory<OracleService, ServiceConfiguration> {
  @Override
  public OracleService getService() {
    return getServiceWithConfig(ServiceConfiguration.configure().compile());
  }

  @Override
  protected OracleService getServiceWithConfig(ServiceConfiguration config) {
    return new OracleServiceImpl(config);
  }
}
