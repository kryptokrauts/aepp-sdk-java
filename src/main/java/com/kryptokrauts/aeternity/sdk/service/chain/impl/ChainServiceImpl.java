package com.kryptokrauts.aeternity.sdk.service.chain.impl;

import javax.annotation.Nonnull;

import com.kryptokrauts.aeternity.generated.api.ChainApiImpl;
import com.kryptokrauts.aeternity.generated.api.rxjava.ChainApi;
import com.kryptokrauts.aeternity.generated.model.KeyBlock;
import com.kryptokrauts.aeternity.sdk.service.ServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.chain.ChainService;

import io.reactivex.Observable;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChainServiceImpl implements ChainService {
    @Nonnull
    private ServiceConfiguration config;

    private ChainApi chainApi;

    private ChainApi getChainApi() {
        if ( chainApi == null ) {
            chainApi = new ChainApi( new ChainApiImpl( config.getApiClient() ) );
        }
        return chainApi;
    }

    @Override
    public Observable<KeyBlock> getCurrentKeyBlock() {
        return getChainApi().rxGetCurrentKeyBlock().toObservable();
    }
}
