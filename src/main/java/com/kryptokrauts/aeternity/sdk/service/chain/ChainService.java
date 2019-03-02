package com.kryptokrauts.aeternity.sdk.service.chain;

import com.kryptokrauts.aeternity.generated.model.KeyBlock;
import io.reactivex.Single;

public interface ChainService {

  Single<KeyBlock> getCurrentKeyBlock();
}
