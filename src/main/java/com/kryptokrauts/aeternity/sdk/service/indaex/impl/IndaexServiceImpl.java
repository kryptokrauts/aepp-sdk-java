package com.kryptokrauts.aeternity.sdk.service.indaex.impl;

import com.kryptokrauts.aeternity.sdk.service.indaex.IndaexService;
import com.kryptokrauts.indaex.generated.api.rxjava.MiddlewareApi;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class IndaexServiceImpl implements IndaexService {

  @NonNull private MiddlewareApi indaexApi;
}
