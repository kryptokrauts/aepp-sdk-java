package com.kryptokrauts.aeternity.sdk.service.mdw;

import com.kryptokrauts.aeternity.sdk.service.mdw.domain.AuctionSortBy;
import com.kryptokrauts.aeternity.sdk.service.mdw.domain.NameAuctionResult;
import com.kryptokrauts.aeternity.sdk.service.mdw.domain.NameAuctionsResult;
import com.kryptokrauts.aeternity.sdk.service.mdw.domain.SortDirection;
import com.kryptokrauts.aeternity.sdk.service.mdw.domain.StatusResult;
import java.math.BigInteger;

public interface MiddlewareService {

  StatusResult blockingGetStatus();

  NameAuctionResult blockingGetNameAuction(String name);

  NameAuctionsResult blockingGetNameAuctions();

  NameAuctionsResult blockingGetNameAuctions(
      AuctionSortBy sortBy, SortDirection sortDirection, BigInteger page, BigInteger limit);
}
