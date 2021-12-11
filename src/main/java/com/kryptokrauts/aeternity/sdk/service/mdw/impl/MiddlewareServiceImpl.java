package com.kryptokrauts.aeternity.sdk.service.mdw.impl;

import com.kryptokrauts.aeternity.sdk.service.mdw.MiddlewareService;
import com.kryptokrauts.aeternity.sdk.service.mdw.domain.AuctionSortBy;
import com.kryptokrauts.aeternity.sdk.service.mdw.domain.NameAuctionResult;
import com.kryptokrauts.aeternity.sdk.service.mdw.domain.NameAuctionsResult;
import com.kryptokrauts.aeternity.sdk.service.mdw.domain.SortDirection;
import com.kryptokrauts.aeternity.sdk.service.mdw.domain.StatusResult;
import com.kryptokrauts.mdw.generated.api.rxjava.MiddlewareApi;
import java.math.BigInteger;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MiddlewareServiceImpl implements MiddlewareService {

  @NonNull private MiddlewareApi mdwApi;

  @Override
  public StatusResult blockingGetStatus() {
    return StatusResult.builder().build().blockingGet(mdwApi.rxGetStatus());
  }

  @Override
  public NameAuctionResult blockingGetNameAuction(String name) {
    return NameAuctionResult.builder().build().blockingGet(mdwApi.rxGetNameAuctionById(name));
  }

  @Override
  public NameAuctionsResult blockingGetNameAuctions() {
    return this.blockingGetNameAuctions(
        AuctionSortBy.EXPIRATION, SortDirection.BACKWARD, BigInteger.ONE, BigInteger.valueOf(100));
  }

  @Override
  public NameAuctionsResult blockingGetNameAuctions(
      AuctionSortBy sortBy, SortDirection sortDirection, BigInteger page, BigInteger limit) {
    return NameAuctionsResult.builder()
        .build()
        .blockingGet(
            mdwApi.rxGetAllAuctions(sortBy.toString(), sortDirection.toString(), page, limit));
  }
}
