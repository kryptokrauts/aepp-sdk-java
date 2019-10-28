package com.kryptokrauts.aeternity.sdk.service.aeternal.domain;

import com.kryptokrauts.aeternal.generated.model.ActiveAuctions;
import com.kryptokrauts.aeternity.sdk.domain.GenericResultObject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@ToString
public class ActiveAuctionsResult
    extends GenericResultObject<ActiveAuctions, ActiveAuctionsResult> {

  @NonNull @Default private List<ActiveAuctionResult> activeAuctionResults = new ArrayList<>();

  @Override
  protected ActiveAuctionsResult map(ActiveAuctions generatedResultObject) {
    if (generatedResultObject != null)
      return this.toBuilder()
          .activeAuctionResults(
              generatedResultObject.stream()
                  .map(result -> ActiveAuctionResult.builder().build().map(result))
                  .collect(Collectors.toList()))
          .build();
    else return this.toBuilder().build();
  }

  @Override
  protected String getResultObjectClassName() {
    return this.getClass().getName();
  }
}
