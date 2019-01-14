package com.kryptokrauts.aeternity.sdk.service;

import com.kryptokrauts.aeternity.generated.epoch.api.ChainApiImpl;
import com.kryptokrauts.aeternity.generated.epoch.api.rxjava.ChainApi;
import com.kryptokrauts.aeternity.generated.epoch.model.KeyBlock;
import io.reactivex.Observable;

/**
 * @TODO refactor to factory pattern
 */
public class ChainService {

    private static ChainService instance;

    private ChainApi chainApi;

    public static ChainService getInstance() {
        if (instance == null) {
            instance = new ChainService();
        }
        instance.chainApi = new ChainApi(new ChainApiImpl());
        return instance;
    }

    public Observable<KeyBlock> getCurrentKeyBlock() {
        return chainApi.rxGetCurrentKeyBlock().toObservable();
    }
}
