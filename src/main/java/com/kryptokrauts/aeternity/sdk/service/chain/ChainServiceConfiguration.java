package com.kryptokrauts.aeternity.sdk.service.chain;

import java.util.HashMap;

import com.google.common.collect.ImmutableMap;
import com.kryptokrauts.aeternity.generated.epoch.ApiClient;
import com.kryptokrauts.aeternity.generated.epoch.api.ChainApiImpl;
import com.kryptokrauts.aeternity.generated.epoch.api.rxjava.ChainApi;
import com.kryptokrauts.aeternity.sdk.constants.BaseConstants;
import com.kryptokrauts.aeternity.sdk.service.config.ServiceConfiguration;

import io.vertx.core.json.JsonObject;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
// @Builder( builderMethodName = "configure", buildMethodName = "compile" )
@SuperBuilder
public class ChainServiceConfiguration extends ServiceConfiguration {

    public ChainApi getChainApi() {
        return new ChainApi( new ChainApiImpl( new ApiClient( vertx, new JsonObject( new HashMap<String, Object>( ImmutableMap
        .of( BaseConstants.VERTX_BASE_PATH, base_url ) ) ) ) ) );
    }
}
