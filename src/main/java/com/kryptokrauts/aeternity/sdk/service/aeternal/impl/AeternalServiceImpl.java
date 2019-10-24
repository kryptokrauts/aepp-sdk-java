package com.kryptokrauts.aeternity.sdk.service.aeternal.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kryptokrauts.aeternal.generated.api.rxjava.DefaultApi;
import com.kryptokrauts.aeternity.sdk.service.aeternal.AeternalService;
import com.kryptokrauts.aeternity.sdk.service.aeternal.order.NameSortBy;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AeternalServiceImpl implements AeternalService {

  @NonNull private DefaultApi aeternalApi;

  @Override
  public Object blockingGetStatus() {
    return aeternalApi.rxGetMdwStatus().blockingGet();
  }

  @Override
  public Object blockingGetNameAuctionsActive() {
    return this.blockingGetNameAuctionsActive(
        Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
  }

  @Override
  public Object blockingGetNameAuctionsActive(
      Optional<Integer> length,
      Optional<String> reverse,
      Optional<Integer> limit,
      Optional<Integer> page,
      Optional<NameSortBy> sortBy) {
    return aeternalApi
        .rxGetActiveNameAuctions(
            length.orElse(null),
            reverse.orElse(null),
            limit.orElse(null),
            page.orElse(null),
            sortBy.orElse(NameSortBy.EXPIRATION).toString())
        .blockingGet();
  }

  @Override
  public boolean isAuctionActive(String name) throws IOException {
    Object result = this.blockingGetNameAuctionsActive();
    ObjectMapper objectMapper = new ObjectMapper();
    List<Map<String, String>> auctions =
        objectMapper.readValue(objectMapper.writeValueAsString(result), List.class);
    return auctions.stream()
        .filter(auction -> auction.get("name").equals(name))
        .findAny()
        .isPresent();
  }
}
