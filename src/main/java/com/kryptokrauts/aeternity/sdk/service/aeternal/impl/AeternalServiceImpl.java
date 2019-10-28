package com.kryptokrauts.aeternity.sdk.service.aeternal.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kryptokrauts.aeternal.generated.api.rxjava.DefaultApi;
import com.kryptokrauts.aeternity.sdk.service.aeternal.AeternalService;
import com.kryptokrauts.aeternity.sdk.service.aeternal.domain.ActiveAuctionsResult;
import com.kryptokrauts.aeternity.sdk.service.aeternal.domain.NameSortBy;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AeternalServiceImpl implements AeternalService {

  @NonNull private DefaultApi aeternalApi;

  private ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public Object blockingGetStatus() {
    return aeternalApi.rxGetMdwStatus().blockingGet();
  }

  @Override
  public BigInteger blockingGetNameAuctionsActiveCount() throws IOException {
    Object result =
        aeternalApi.rxGetActiveNameAuctionsCount(null, null, null, null, null).blockingGet();
    String resultJson = objectMapper.writeValueAsString(result);
    JsonNode jsonNode = objectMapper.readTree(resultJson);
    return new BigInteger(jsonNode.get("count").toString());
  }

  @Override
  public ActiveAuctionsResult blockingGetNameAuctionsActive() {
    return this.blockingGetNameAuctionsActive(
        Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
  }

  @Override
  public ActiveAuctionsResult blockingGetNameAuctionsActive(
      Optional<BigInteger> length,
      Optional<String> reverse,
      Optional<BigInteger> limit,
      Optional<BigInteger> page,
      Optional<NameSortBy> sortBy) {
    return ActiveAuctionsResult.builder()
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
  public boolean blockingIsAuctionActive(String name) {
    ActiveAuctionsResult result = this.blockingGetNameAuctionsActive();
    return result.getActiveAuctionResults().stream()
        .filter(auction -> auction.getName().equals(name))
        .findAny()
        .isPresent();
  }
}
