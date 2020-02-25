package com.kryptokrauts.aeternity.sdk.service.aeternal;

import com.kryptokrauts.aeternity.sdk.domain.ObjectResultWrapper;
import com.kryptokrauts.aeternity.sdk.service.aeternal.domain.ActiveNameAuctionsCountResult;
import com.kryptokrauts.aeternity.sdk.service.aeternal.domain.ActiveNameAuctionsResult;
import com.kryptokrauts.aeternity.sdk.service.aeternal.domain.ActiveNamesResult;
import com.kryptokrauts.aeternity.sdk.service.aeternal.domain.NameSortBy;
import java.math.BigInteger;
import java.util.Optional;

public interface AeternalService {

  ObjectResultWrapper blockingGetMdwStatus();

  ActiveNameAuctionsCountResult blockingGetActiveNameAuctionsCount();

  ActiveNameAuctionsResult blockingGetActiveNameAuctions();

  ActiveNameAuctionsResult blockingGetActiveNameAuctions(
      Optional<BigInteger> length,
      Optional<String> reverse,
      Optional<BigInteger> limit,
      Optional<BigInteger> page,
      Optional<NameSortBy> sortBy);

  boolean blockingIsNameAuctionActive(String name);

  ActiveNamesResult blockingGetActiveNames();

  ActiveNamesResult blockingGetActiveNames(
      Optional<BigInteger> limit, Optional<BigInteger> page, Optional<String> account);

  ActiveNamesResult blockingSearchName(String name);
}
