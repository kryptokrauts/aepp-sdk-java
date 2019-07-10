package com.kryptokrauts.aeternity.sdk.service.aens.impl;

import com.kryptokrauts.aeternity.generated.api.NameServiceApiImpl;
import com.kryptokrauts.aeternity.generated.api.rxjava.NameServiceApi;
import com.kryptokrauts.aeternity.generated.model.NameEntry;
import com.kryptokrauts.aeternity.sdk.service.ServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.aens.NameService;
import io.reactivex.Single;
import javax.annotation.Nonnull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class NameServiceImpl implements NameService {

  @Nonnull private ServiceConfiguration config;

  private NameServiceApi namesServiceApi;

  private NameServiceApi getNameServiceApi() {
    if (namesServiceApi == null) {
      namesServiceApi = new NameServiceApi(new NameServiceApiImpl(config.getApiClient()));
    }
    return namesServiceApi;
  }

  @Override
  public Single<NameEntry> getNameId(final String name) {
    return getNameServiceApi().rxGetNameEntryByName(name);
  }
}
