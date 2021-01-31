package com.kryptokrauts.aeternity.sdk.service.indaex;

import com.kryptokrauts.aeternity.sdk.service.indaex.domain.*;
import java.math.BigInteger;

public interface IndaexService {

  StatusResult blockingGetStatus();

  NameAuctionResult blockingGetNameAuction(String name);

  NameAuctionsResult blockingGetNameAuctions();

  NameAuctionsResult blockingGetNameAuctions(
      AuctionSortBy sortBy, SortDirection sortDirection, BigInteger page, BigInteger limit);
}
