package com.kryptokrauts.aeternity.sdk.service.compiler;

import com.kryptokrauts.aeternity.sdk.service.AbstractServiceFactory;
import com.kryptokrauts.aeternity.sdk.service.ServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.compiler.impl.SophiaCompilerServiceImpl;

public class CompilerServiceFactory
    extends AbstractServiceFactory<CompilerService, ServiceConfiguration> {

  @Override
  public CompilerService getService() {
    return getService(ServiceConfiguration.configure().compile());
  }

  @Override
  protected CompilerService getServiceWithConfig(ServiceConfiguration config) {
    return new SophiaCompilerServiceImpl(config);
  }
}
