package com.kryptokrauts.aeternity.sdk.service.wallet;

import com.kryptokrauts.aeternity.sdk.service.AbstractServiceFactory;
import com.kryptokrauts.aeternity.sdk.service.wallet.impl.WalletServiceImpl;

public class WalletServiceFactory
    extends AbstractServiceFactory<WalletService, WalletServiceConfiguration> {
  @Override
  public WalletService getService() {
    return getServiceWithConfig(WalletServiceConfiguration.configure().compile());
  }

  @Override
  public WalletService getServiceWithConfig(WalletServiceConfiguration config) {
    return new WalletServiceImpl(config);
  }
}
