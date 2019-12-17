package com.kryptokrauts.aeternity.sdk.service.aeternal.impl;

import com.kryptokrauts.aeternal.generated.api.rxjava.DefaultApi;
import com.kryptokrauts.aeternity.sdk.domain.ObjectResultWrapper;
import com.kryptokrauts.aeternity.sdk.service.aeternal.AeternalService;
import com.kryptokrauts.aeternity.sdk.service.aeternal.domain.ActiveNameAuctionsCountResult;
import com.kryptokrauts.aeternity.sdk.service.aeternal.domain.ActiveNameAuctionsResult;
import com.kryptokrauts.aeternity.sdk.service.aeternal.domain.ActiveNamesResult;
import com.kryptokrauts.aeternity.sdk.service.aeternal.domain.NameSortBy;
import java.math.BigInteger;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AeternalServiceImpl implements AeternalService {

  @NonNull private DefaultApi aeternalApi;

  @Override
  public ObjectResultWrapper blockingGetMdwStatus() {
    return ObjectResultWrapper.builder().build().blockingGet(aeternalApi.rxGetMdwStatus());
  }

  @Override
  public ActiveNameAuctionsCountResult blockingGetActiveNameAuctionsCount() {
    return ActiveNameAuctionsCountResult.builder()
        .build()
        .blockingGet(aeternalApi.rxGetActiveNameAuctionsCount(null, null, null, null, null));
  }

  @Override
  public ActiveNameAuctionsResult blockingGetActiveNameAuctions() {
    return this.blockingGetActiveNameAuctions(
        Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
  }

  @Override
  public ActiveNameAuctionsResult blockingGetActiveNameAuctions(
      Optional<BigInteger> length,
      Optional<String> reverse,
      Optional<BigInteger> limit,
      Optional<BigInteger> page,
      Optional<NameSortBy> sortBy) {
    return ActiveNameAuctionsResult.builder()
        .build()
        .blockingGet(
            aeternalApi.rxGetActiveNameAuctions(
                length.orElse(null),
                reverse.orElse(null),
                limit.orElse(null),
                page.orElse(null),
                sortBy.orElse(NameSortBy.EXPIRATION).toString()));
  }

  @Override
  public boolean blockingIsNameAuctionActive(String name) {
    ActiveNameAuctionsResult result = this.blockingGetActiveNameAuctions();
    return result.getActiveNameAuctionResults().stream()
        .filter(auction -> auction.getName().equalsIgnoreCase(name))
        .findAny()
        .isPresent();
  }

  @Override
  public ActiveNamesResult blockingGetActiveNames() {
    return this.blockingGetActiveNames(Optional.empty(), Optional.empty(), Optional.empty());
  }

  @Override
  public ActiveNamesResult blockingGetActiveNames(
      Optional<BigInteger> limit, Optional<BigInteger> page, Optional<String> account) {
    return ActiveNamesResult.builder()
        .build()
        .blockingGet(
            aeternalApi.rxGetActiveNames(
                limit.orElse(null), page.orElse(null), account.orElse(null)));
  }

  @Override
  public ActiveNamesResult blockingSearchName(String name) {
    return ActiveNamesResult.builder().build().blockingGet(aeternalApi.rxSearchName(name));
  }
}
