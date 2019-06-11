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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(VertxUnitRunner.class)
public abstract class BaseTest {

  protected static final Logger _logger = LoggerFactory.getLogger("IntegrationTest");

  private static final String AETERNITY_BASE_URL = "AETERNITY_BASE_URL";

  private static final String COMPILER_BASE_URL = "COMPILER_BASE_URL";

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
                    .
                    // we adapt the minimal gas price to make sure, that the create contract tx has
                    // enough aeons for the fee
                    minimalGasPrice(1011000000l)
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

  protected static String getCompilerBaseUrl() throws ConfigurationException {
    String compilerBaseUrl = System.getenv(COMPILER_BASE_URL);
    if (compilerBaseUrl == null) {
      throw new ConfigurationException("ENV variable missing: COMPILER_BASE_URL");
    }
    return compilerBaseUrl;
  }

  @BeforeClass
  public static void startup() throws ConfigurationException {
    _logger.info(
        String.format(
            "--------------------------- %s ---------------------------",
            "Using following environment"));
    _logger.info(String.format("%s: %s", AETERNITY_BASE_URL, getAeternityBaseUrl()));
    _logger.info(String.format("%s: %s", COMPILER_BASE_URL, getCompilerBaseUrl()));
    _logger.info(
        "-----------------------------------------------------------------------------------");
  }
}
