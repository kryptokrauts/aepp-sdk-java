package com.kryptokrauts.aeternity.sdk.service.aeternal.domain;

import com.kryptokrauts.aeternal.generated.model.ActiveAuction;
import com.kryptokrauts.aeternity.sdk.domain.GenericResultObject;
import java.math.BigInteger;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@ToString
public class ActiveAuctionResult extends GenericResultObject<ActiveAuction, ActiveAuctionResult> {

  private String name;
  private BigInteger expiration;
  private BigInteger winningBid;
  private String winningBidder;

  @Override
  protected ActiveAuctionResult map(ActiveAuction generatedResultObject) {
    if (generatedResultObject != null)
      return this.toBuilder()
          .name(generatedResultObject.getName())
          .expiration(generatedResultObject.getExpiration())
          .winningBid(generatedResultObject.getWinningBid())
          .winningBidder(generatedResultObject.getWinningBidder())
          .build();
    else return this.toBuilder().build();
  }

  @Override
  protected String getResultObjectClassName() {
    return ActiveAuctionResult.class.getName();
  }
}
