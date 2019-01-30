package com.kryptokrauts.aeternity.generated.epoch.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import com.kryptokrauts.aeternity.sdk.service.chain.ChainService;
import com.kryptokrauts.aeternity.sdk.service.chain.ChainServiceConfiguration;
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

@RunWith( VertxUnitRunner.class )
public abstract class BaseTest {

    private static final String CONFIG_PROPERTIES = "config.properties";

    private static final String EPOCH_BASE_URL = "epoch.api.baseUrl";

    protected static final String BENEFICIARY_PRIVATE_KEY = "79816BBF860B95600DDFABF9D81FEE81BDB30BE823B17D80B9E48BE0A7015ADF";

    protected KeyPairService keyPairService;

    protected ChainService chainService;

    protected TransactionService transactionServiceNative;

    protected TransactionService transactionServiceDebug;

    @Rule
    public RunTestOnContext rule = new RunTestOnContext();

    @Before
    public void setupApiClient( TestContext context ) {
        Vertx vertx = rule.vertx();
        keyPairService = new KeyPairServiceFactory().getService();
        chainService = new ChainServiceFactory().getService( ChainServiceConfiguration.builder().base_url( getEpochBaseUrl() ).vertx( vertx ).build() );
        transactionServiceNative = new TransactionServiceFactory()
        .getService( TransactionServiceConfiguration.builder().base_url( getEpochBaseUrl() ).vertx( vertx ).build() );
        transactionServiceDebug = new TransactionServiceFactory()
        .getService( TransactionServiceConfiguration.builder().nativeMode( false ).base_url( getEpochBaseUrl() ).vertx( vertx ).build() );
    }

    private String getEpochBaseUrl() {
        String epochBaseUrl = null;
        final Properties properties = new Properties();
        try (InputStream inputStream = BaseTest.class.getClassLoader().getResourceAsStream( CONFIG_PROPERTIES )) {
            if ( inputStream == null ) {
                throw new IOException( CONFIG_PROPERTIES + " not found" );
            }
            properties.load( inputStream );
            epochBaseUrl = properties.getProperty( EPOCH_BASE_URL );
        }
        catch ( IOException ignored ) {}
        return epochBaseUrl;
    }
}
