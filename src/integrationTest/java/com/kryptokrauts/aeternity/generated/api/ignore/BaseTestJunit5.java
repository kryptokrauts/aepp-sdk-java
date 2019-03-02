package com.kryptokrauts.aeternity.generated.api.ignore;

import com.kryptokrauts.aeternity.generated.Configuration;
import com.kryptokrauts.aeternity.generated.api.*;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Ignore // problems with testcontext ...
public abstract class BaseTestJunit5 {

  private static final String CONFIG_PROPERTIES = "config.properties";
  private static final String EPOCH_BASE_URL = "epoch.api.baseUrl";

  private AccountApi accountApi;
  private ChainApi chainApi;
  private ChannelApi channelApi;
  private ContractApi contractApi;
  private DebugApi debugApi;
  private ExternalApi externalApi;
  private InternalApi internalApi;
  private NameServiceApi nameServiceApi;
  private NodeInfoApi nodeInfoApi;
  private OracleApi oracleApi;
  private TransactionApi transactionApi;

  @BeforeAll
  void prepare(Vertx vertx, VertxTestContext context) {
    Configuration.setupDefaultApiClient(vertx, getConfig());
    accountApi = new AccountApiImpl();
    chainApi = new ChainApiImpl();
    channelApi = new ChannelApiImpl();
    contractApi = new ContractApiImpl();
    debugApi = new DebugApiImpl();
    externalApi = new ExternalApiImpl();
    internalApi = new InternalApiImpl();
    nameServiceApi = new NameServiceApiImpl();
    nodeInfoApi = new NodeInfoApiImpl();
    oracleApi = new OracleApiImpl();
    transactionApi = new TransactionApiImpl();
  }

  private JsonObject getConfig() {
    JsonObject config = new JsonObject();
    config.put("basePath", getEpochBaseUrl());
    return config;
  }

  private String getEpochBaseUrl() {
    String epochBaseUrl = null;
    final Properties properties = new Properties();
    try (InputStream inputStream =
        BaseTestJunit5.class.getClassLoader().getResourceAsStream(CONFIG_PROPERTIES)) {
      if (inputStream == null) {
        throw new IOException(CONFIG_PROPERTIES + " not found");
      }
      properties.load(inputStream);
      epochBaseUrl = properties.getProperty(EPOCH_BASE_URL);
    } catch (IOException ignored) {
    }
    return epochBaseUrl;
  }

  public AccountApi getAccountApi() {
    return accountApi;
  }

  public ChainApi getChainApi() {
    return chainApi;
  }

  public ChannelApi getChannelApi() {
    return channelApi;
  }

  public ContractApi getContractApi() {
    return contractApi;
  }

  public DebugApi getDebugApi() {
    return debugApi;
  }

  public ExternalApi getExternalApi() {
    return externalApi;
  }

  public InternalApi getInternalApi() {
    return internalApi;
  }

  public NameServiceApi getNameServiceApi() {
    return nameServiceApi;
  }

  public NodeInfoApi getNodeInfoApi() {
    return nodeInfoApi;
  }

  public OracleApi getOracleApi() {
    return oracleApi;
  }

  public TransactionApi getTransactionApi() {
    return transactionApi;
  }
}
