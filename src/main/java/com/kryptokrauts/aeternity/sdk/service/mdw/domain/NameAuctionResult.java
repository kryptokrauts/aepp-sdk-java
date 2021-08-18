package com.kryptokrauts.aeternity.sdk.service.mdw.domain;

import com.kryptokrauts.aeternity.sdk.domain.GenericResultObject;
import com.kryptokrauts.mdw.generated.model.NameAuction;
import java.math.BigInteger;
import java.util.List;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@ToString
public class NameAuctionResult extends GenericResultObject<NameAuction, NameAuctionResult> {

  private String nameHash;
  private String name;
  private BigInteger auctionEnd;
  private List<BigInteger> bidIndexes;
  private NameClaimDetailModel currentBid;

  @Override
  protected NameAuctionResult map(NameAuction generatedResultObject) {
    if (generatedResultObject != null) {
      this.toBuilder()
          .nameHash(generatedResultObject.getHash())
          .name(generatedResultObject.getName())
          .auctionEnd(generatedResultObject.getInfo().getAuctionEnd())
          .bidIndexes(generatedResultObject.getInfo().getBids())
          .currentBid(
              NameClaimDetailModel.builder()
                  .build()
                  .map(generatedResultObject.getInfo().getLastBid()))
          .build();
    }
    return this.toBuilder().build();
  }

  @Override
  protected String getResultObjectClassName() {
    return NameAuctionResult.class.getName();
  }
}
