package com.kryptokrauts.aeternity.sdk.service.chain;

import com.kryptokrauts.aeternity.generated.epoch.model.KeyBlock;

import io.reactivex.Observable;

public interface ChainService {

    public Observable<KeyBlock> getCurrentKeyBlock();

}
