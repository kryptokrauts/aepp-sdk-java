package com.kryptokrauts.aeternity.sdk.service.transaction;

import java.util.HashMap;

import com.google.common.collect.ImmutableMap;
import com.kryptokrauts.aeternity.generated.epoch.ApiClient;
import com.kryptokrauts.aeternity.generated.epoch.api.TransactionApiImpl;
import com.kryptokrauts.aeternity.generated.epoch.api.rxjava.TransactionApi;
import com.kryptokrauts.aeternity.sdk.constants.BaseConstants;
import com.kryptokrauts.aeternity.sdk.constants.Network;
import com.kryptokrauts.aeternity.sdk.service.config.ServiceConfiguration;

import io.vertx.core.json.JsonObject;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class TransactionServiceConfiguration extends ServiceConfiguration {

    @Default
    private boolean nativeMode = true;

    @Default
    private Network network = Network.TESTNET;

    public TransactionApi getTransactionApi() {
        return new TransactionApi( new TransactionApiImpl( new ApiClient( vertx, new JsonObject( new HashMap<String, Object>( ImmutableMap
        .of( BaseConstants.VERTX_BASE_PATH, base_url ) ) ) ) ) );
    }
}
