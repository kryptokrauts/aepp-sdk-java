package com.kryptokrauts.aeternity.generated.api;

import com.kryptokrauts.aeternity.sdk.constants.Network;
import com.kryptokrauts.aeternity.sdk.service.ServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.account.AccountService;
import com.kryptokrauts.aeternity.sdk.service.account.AccountServiceFactory;
import com.kryptokrauts.aeternity.sdk.service.chain.ChainService;
import com.kryptokrauts.aeternity.sdk.service.chain.ChainServiceFactory;
import com.kryptokrauts.aeternity.sdk.service.keypair.KeyPairService;
import com.kryptokrauts.aeternity.sdk.service.keypair.KeyPairServiceFactory;
import com.kryptokrauts.aeternity.sdk.service.transaction.TransactionService;
import com.kryptokrauts.aeternity.sdk.service.transaction.TransactionServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.transaction.TransactionServiceFactory;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public abstract class BaseTest {

  private static final String CONFIG_PROPERTIES = "config.properties";

  private static final String API_BASE_URL = "api.baseUrl";

  protected static final String BENEFICIARY_PRIVATE_KEY =
      "79816BBF860B95600DDFABF9D81FEE81BDB30BE823B17D80B9E48BE0A7015ADF";

  protected KeyPairService keyPairService;

  protected ChainService chainService;

  protected TransactionService transactionServiceNative;

  protected TransactionService transactionServiceDebug;

  protected AccountService accountService;

  @Rule public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void setupApiClient(TestContext context) {
    Vertx vertx = rule.vertx();
    keyPairService = new KeyPairServiceFactory().getService();
    accountService =
        new AccountServiceFactory()
            .getService(
                ServiceConfiguration.configure()
                    .baseUrl(getAeternityBaseUrl())
                    .vertx(vertx)
                    .compile());
    chainService =
        new ChainServiceFactory()
            .getService(
                ServiceConfiguration.configure()
                    .baseUrl(getAeternityBaseUrl())
                    .vertx(vertx)
                    .compile());
    transactionServiceNative =
        new TransactionServiceFactory()
            .getService(
                TransactionServiceConfiguration.configure()
                    .baseUrl(getAeternityBaseUrl())
                    .network(Network.DEVNET)
                    .vertx(vertx)
                    .compile());
    transactionServiceDebug =
        new TransactionServiceFactory()
            .getService(
                TransactionServiceConfiguration.configure()
                    .nativeMode(false)
                    .baseUrl(getAeternityBaseUrl())
                    .network(Network.DEVNET)
                    .vertx(vertx)
                    .compile());
  }

  private String getAeternityBaseUrl() {
    String aeternityBaseUrl = null;
    final Properties properties = new Properties();
    try (InputStream inputStream =
        BaseTest.class.getClassLoader().getResourceAsStream(CONFIG_PROPERTIES)) {
      if (inputStream == null) {
        throw new IOException(CONFIG_PROPERTIES + " not found");
      }
      properties.load(inputStream);
      aeternityBaseUrl = properties.getProperty(API_BASE_URL);
    } catch (IOException ignored) {
    }
    return aeternityBaseUrl;
  }
}
