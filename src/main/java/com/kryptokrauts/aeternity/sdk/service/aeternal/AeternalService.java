package com.kryptokrauts.aeternity.sdk.service.aeternal;

import com.kryptokrauts.aeternity.sdk.service.aeternal.domain.ActiveAuctionsResult;
import com.kryptokrauts.aeternity.sdk.service.aeternal.order.NameSortBy;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Optional;

public interface AeternalService {

  Object blockingGetStatus();

  BigInteger blockingGetNameAuctionsActiveCount() throws IOException;

  ActiveAuctionsResult blockingGetNameAuctionsActive();

  ActiveAuctionsResult blockingGetNameAuctionsActive(
      Optional<BigInteger> length,
      Optional<String> reverse,
      Optional<BigInteger> limit,
      Optional<BigInteger> page,
      Optional<NameSortBy> sortBy);

  boolean blockingIsAuctionActive(String name);
}
