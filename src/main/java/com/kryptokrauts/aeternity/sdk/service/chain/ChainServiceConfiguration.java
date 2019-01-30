package com.kryptokrauts.aeternity.sdk.service.chain;

import com.kryptokrauts.aeternity.generated.epoch.api.ChainApiImpl;
import com.kryptokrauts.aeternity.generated.epoch.api.rxjava.ChainApi;
import com.kryptokrauts.aeternity.sdk.service.config.ServiceConfiguration;

import lombok.Builder.Default;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
// @Builder( builderMethodName = "configure", buildMethodName = "compile" )
@SuperBuilder
public class ChainServiceConfiguration extends ServiceConfiguration {
    @Default
    private ChainApi chainApi = new ChainApi( new ChainApiImpl() );
}
