package com.kryptokrauts.aeternity.sdk.service.name.impl;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.sdk.service.aeternity.AeternityServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.name.NameService;
import com.kryptokrauts.aeternity.sdk.service.name.domain.NameEntryResult;
import io.reactivex.Single;
import javax.annotation.Nonnull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class NameServiceImpl implements NameService {

  @Nonnull private AeternityServiceConfiguration config;

  @Nonnull private ExternalApi externalApi;

  @Override
  public Single<NameEntryResult> asyncGetNameId(final String name) {
    return NameEntryResult.builder().build().asyncGet(this.externalApi.rxGetNameEntryByName(name));
  }

  @Override
  public NameEntryResult blockingGetNameId(String name) {
    return NameEntryResult.builder()
        .build()
        .blockingGet(this.externalApi.rxGetNameEntryByName(name));
  }
}
