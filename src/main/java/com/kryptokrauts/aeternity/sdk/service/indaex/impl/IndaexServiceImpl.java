package com.kryptokrauts.aeternity.sdk.service.indaex.impl;

import com.kryptokrauts.aeternity.sdk.service.indaex.IndaexService;
import com.kryptokrauts.aeternity.sdk.service.indaex.domain.AuctionSortBy;
import com.kryptokrauts.aeternity.sdk.service.indaex.domain.NameAuctionResult;
import com.kryptokrauts.aeternity.sdk.service.indaex.domain.NameAuctionsResult;
import com.kryptokrauts.aeternity.sdk.service.indaex.domain.SortDirection;
import com.kryptokrauts.aeternity.sdk.service.indaex.domain.StatusResult;
import com.kryptokrauts.indaex.generated.api.rxjava.MiddlewareApi;
import java.math.BigInteger;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class IndaexServiceImpl implements IndaexService {

  @NonNull private MiddlewareApi indaexApi;

  @Override
  public StatusResult blockingGetStatus() {
    return StatusResult.builder().build().blockingGet(indaexApi.rxGetStatus(null));
  }

  @Override
  public NameAuctionResult blockingGetNameAuction(String name) {
    return NameAuctionResult.builder()
        .build()
        .blockingGet(indaexApi.rxGetNameAuctionById(name, null));
  }

  @Override
  public NameAuctionsResult blockingGetNameAuctions() {
    return this.blockingGetNameAuctions(
        AuctionSortBy.EXPIRATION, SortDirection.BACKWARD, BigInteger.ONE, BigInteger.valueOf(1000));
  }

  @Override
  public NameAuctionsResult blockingGetNameAuctions(
      AuctionSortBy sortBy, SortDirection sortDirection, BigInteger page, BigInteger limit) {
    return NameAuctionsResult.builder()
        .build()
        .blockingGet(
            indaexApi.rxGetAllAuctions(
                sortBy.toString(), sortDirection.toString(), page, limit, null));
  }
}
