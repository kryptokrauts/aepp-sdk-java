package com.kryptokrauts.aeternity.generated.epoch.api;

import com.kryptokrauts.aeternity.generated.epoch.Configuration;
import com.kryptokrauts.aeternity.sdk.AEKit;
import com.kryptokrauts.aeternity.sdk.config.Network;
import com.kryptokrauts.aeternity.sdk.keypair.service.KeyPairService;
import com.kryptokrauts.aeternity.sdk.service.ChainService;
import com.kryptokrauts.aeternity.sdk.service.TransactionService;
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

    protected static final String BENEFICIARY_PRIVATE_KEY = "79816BBF860B95600DDFABF9D81FEE81BDB30BE823B17D80B9E48BE0A7015ADF";
    protected KeyPairService keyPairService;
    protected ChainService chainService;
    protected TransactionService transactionService;

    @Rule
    public RunTestOnContext rule = new RunTestOnContext();

    @Before
    public void setupApiClient(TestContext context) {
        Vertx vertx = rule.vertx();
        Configuration.setupDefaultApiClient(vertx, getConfig());
        keyPairService = AEKit.getKeyPairService();
        chainService =  AEKit.getChainService();
        transactionService = AEKit.getTransactionService(true, Network.TESTNET);
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
}
