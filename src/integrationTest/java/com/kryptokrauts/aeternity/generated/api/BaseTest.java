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
import javax.naming.ConfigurationException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public abstract class BaseTest {

  private static final String AETERNITY_BASE_URL = "AETERNITY_BASE_URL";

  protected static final String BENEFICIARY_PRIVATE_KEY =
      "79816BBF860B95600DDFABF9D81FEE81BDB30BE823B17D80B9E48BE0A7015ADF";

  protected KeyPairService keyPairService;

  protected ChainService chainService;

  protected TransactionService transactionServiceNative;

  protected TransactionService transactionServiceDebug;

  protected AccountService accountService;

  @Rule public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void setupApiClient(TestContext context) throws ConfigurationException {
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

  private static String getAeternityBaseUrl() throws ConfigurationException {
    String aeternityBaseUrl = System.getenv(AETERNITY_BASE_URL);
    if (aeternityBaseUrl == null) {
      throw new ConfigurationException("ENV variable missing: AETERNITY_BASE_URL");
    }
    return aeternityBaseUrl;
  }

  @BeforeClass
  public static void startup() throws ConfigurationException {
    System.out.println(
        String.format(
            "--------------------------- %s ---------------------------\n",
            "Using following environment"));
    System.out.println(String.format("%s: %s", "AETERNITY_BASE_URL", getAeternityBaseUrl()));
    System.out.println(
        String.format(
            "\n-----------------------------------------------------------------------------------"));
  }
}
