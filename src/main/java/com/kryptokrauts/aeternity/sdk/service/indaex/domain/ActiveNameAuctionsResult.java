package com.kryptokrauts.aeternity.sdk.service.aeternal.domain;

import com.kryptokrauts.aeternal.generated.model.ActiveNameAuctions;
import com.kryptokrauts.aeternity.sdk.domain.GenericResultObject;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@ToString
public class ActiveNameAuctionsResult
    extends GenericResultObject<ActiveNameAuctions, ActiveNameAuctionsResult> {

  private List<ActiveNameAuctionResult> activeNameAuctionResults;

  @Override
  protected ActiveNameAuctionsResult map(ActiveNameAuctions generatedResultObject) {
    if (generatedResultObject != null)
      return this.toBuilder()
          .activeNameAuctionResults(
              generatedResultObject.stream()
                  .map(result -> ActiveNameAuctionResult.builder().build().map(result))
                  .collect(Collectors.toList()))
          .build();
    else return this.toBuilder().build();
  }

  @Override
  protected String getResultObjectClassName() {
    return this.getClass().getName();
  }
}
