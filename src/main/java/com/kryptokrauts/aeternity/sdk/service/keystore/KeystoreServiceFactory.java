package com.kryptokrauts.aeternity.sdk.service.keystore;

import com.kryptokrauts.aeternity.sdk.service.AbstractServiceFactory;
import com.kryptokrauts.aeternity.sdk.service.keystore.impl.KeystoreServiceImpl;

public class KeystoreServiceFactory
    extends AbstractServiceFactory<KeystoreService, KeystoreServiceConfiguration> {
  @Override
  public KeystoreService getService() {
    return getServiceWithConfig(KeystoreServiceConfiguration.configure().compile());
  }

  @Override
  public KeystoreService getServiceWithConfig(KeystoreServiceConfiguration config) {
    return new KeystoreServiceImpl(config);
  }
}
