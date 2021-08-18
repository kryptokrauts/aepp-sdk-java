package com.kryptokrauts.aeternity.sdk.service.mdw.domain;

import com.kryptokrauts.aeternity.sdk.domain.GenericResultObject;
import com.kryptokrauts.mdw.generated.model.NameAuctions;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@ToString
public class NameAuctionsResult extends GenericResultObject<NameAuctions, NameAuctionsResult> {

  private List<NameAuctionResult> nameAuctions;

  @Override
  protected NameAuctionsResult map(NameAuctions generatedResultObject) {
    if (generatedResultObject != null && generatedResultObject.getData() != null) {
      return this.toBuilder()
          .nameAuctions(
              generatedResultObject.getData().stream()
                  .map(nameAuction -> NameAuctionResult.builder().build().map(nameAuction))
                  .collect(Collectors.toList()))
          .build();
    }
    return this.toBuilder().build();
  }

  @Override
  protected String getResultObjectClassName() {
    return NameAuctionsResult.class.getName();
  }
}
