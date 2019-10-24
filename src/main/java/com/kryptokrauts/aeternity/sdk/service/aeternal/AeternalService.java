package com.kryptokrauts.aeternity.sdk.service.aeternal;

import com.kryptokrauts.aeternity.sdk.service.aeternal.order.NameSortBy;
import java.io.IOException;
import java.util.Optional;

public interface AeternalService {
  Object blockingGetStatus();

  Object blockingGetNameAuctionsActive();

  Object blockingGetNameAuctionsActive(
      Optional<Integer> length,
      Optional<String> reverse,
      Optional<Integer> limit,
      Optional<Integer> page,
      Optional<NameSortBy> sortBy);

  boolean isAuctionActive(String name) throws IOException;
}
