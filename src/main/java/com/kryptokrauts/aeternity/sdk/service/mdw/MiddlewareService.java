package com.kryptokrauts.aeternity.sdk.service.mdw;

import com.kryptokrauts.aeternity.sdk.service.mdw.domain.AuctionSortBy;
import com.kryptokrauts.aeternity.sdk.service.mdw.domain.NameAuctionResult;
import com.kryptokrauts.aeternity.sdk.service.mdw.domain.NameAuctionsResult;
import com.kryptokrauts.aeternity.sdk.service.mdw.domain.SortDirection;
import com.kryptokrauts.aeternity.sdk.service.mdw.domain.StatusResult;
import java.math.BigInteger;

public interface MiddlewareService {

  /**
   * synchronously returns the middleware status
   *
   * @return result of {@link StatusResult}
   */
  StatusResult blockingGetStatus();

  /**
   * synchronously returns the result object of a name auction
   *
   * @return result of {@link NameAuctionResult}
   */
  NameAuctionResult blockingGetNameAuction(String name);

  /**
   * synchronously returns the result object of all name auctions
   *
   * @return result of {@link NameAuctionsResult}
   */
  NameAuctionsResult blockingGetNameAuctions();

  /**
   * synchronously returns the result object of all name auctions filtered and paginated by the
   * given criteria
   *
   * @param sortBy sort by criteria
   * @param sortDirection the direction of sorting
   * @param page number of page to return
   * @param limit number of results to return
   * @return result of {@link NameAuctionsResult}
   */
  NameAuctionsResult blockingGetNameAuctions(
      AuctionSortBy sortBy, SortDirection sortDirection, BigInteger page, BigInteger limit);
}
