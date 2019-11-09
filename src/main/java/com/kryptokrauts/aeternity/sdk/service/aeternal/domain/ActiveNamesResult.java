package com.kryptokrauts.aeternity.sdk.service.aeternal.domain;

import com.kryptokrauts.aeternal.generated.model.ActiveNames;
import com.kryptokrauts.aeternity.sdk.domain.GenericResultObject;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@ToString
public class ActiveNamesResult extends GenericResultObject<ActiveNames, ActiveNamesResult> {

  private List<ActiveNameResult> activeNameResults;

  @Override
  protected ActiveNamesResult map(ActiveNames generatedResultObject) {
    if (generatedResultObject != null)
      return this.toBuilder()
          .activeNameResults(
              generatedResultObject.stream()
                  .map(result -> ActiveNameResult.builder().build().map(result))
                  .collect(Collectors.toList()))
          .build();
    else return this.toBuilder().build();
  }

  @Override
  protected String getResultObjectClassName() {
    return this.getClass().getName();
  }
}
