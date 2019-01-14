package com.kryptokrauts.aeternity.generated.epoch.api;

import com.kryptokrauts.aeternity.generated.epoch.Configuration;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@RunWith(VertxUnitRunner.class)
public abstract class BaseTest {

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

    @Rule
    public RunTestOnContext rule = new RunTestOnContext();

    @Before
    public void setupApiClient(TestContext context) {
        Vertx vertx = rule.vertx();
        Configuration.setupDefaultApiClient(vertx, getConfig());
//        accountApi = new AccountApiImpl();
//        chainApi = new ChainApiImpl();
//        channelApi = new ChannelApiImpl();
//        contractApi = new ContractApiImpl();
//        debugApi = new DebugApiImpl();
//        externalApi = new ExternalApiImpl();
//        internalApi = new InternalApiImpl();
//        nameServiceApi = new NameServiceApiImpl();
//        nodeInfoApi = new NodeInfoApiImpl();
//        oracleApi = new OracleApiImpl();
//        transactionApi = new TransactionApiImpl();
    }

    private JsonObject getConfig() {
        JsonObject config = new JsonObject();
        config.put("basePath", getEpochBaseUrl());
        return config;
    }

    private String getEpochBaseUrl() {
        String epochBaseUrl = null;
        final Properties properties = new Properties();
        try (InputStream inputStream = BaseTest.class.getClassLoader().getResourceAsStream(CONFIG_PROPERTIES)) {
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
