package com.kryptokrauts.aeternity.sdk.service.chain.impl;

import javax.annotation.Nonnull;

import com.kryptokrauts.aeternity.generated.epoch.model.KeyBlock;
import com.kryptokrauts.aeternity.sdk.service.chain.ChainService;
import com.kryptokrauts.aeternity.sdk.service.chain.ChainServiceConfiguration;

import io.reactivex.Observable;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChainServiceImpl implements ChainService {
    @Nonnull
    private ChainServiceConfiguration config;

    @Override
    public Observable<KeyBlock> getCurrentKeyBlock() {
        return config.getChainApi().rxGetCurrentKeyBlock().toObservable();
    }
}
