package com.kryptokrauts.aeternity.sdk.service.aeternal.impl;

import com.kryptokrauts.aeternal.generated.api.rxjava.DefaultApi;
import com.kryptokrauts.aeternity.sdk.service.aeternal.IndaexService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class IndaexServiceImpl implements IndaexService {

  @NonNull private DefaultApi indaexApi;
}
