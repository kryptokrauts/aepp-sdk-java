package com.kryptokrauts.aeternity.sdk.service.transaction.domain;

import com.kryptokrauts.aeternity.generated.model.DryRunResults;
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
public class DryRunTransactionResults
    extends GenericResultObject<DryRunResults, DryRunTransactionResults> {

  @NonNull @Default private List<DryRunTransactionResult> results = new ArrayList<>();

  @Override
  protected DryRunTransactionResults map(DryRunResults generatedResultObject) {
    if (generatedResultObject != null)
      return this.toBuilder()
          .results(
              generatedResultObject.getResults().stream()
                  .map(result -> DryRunTransactionResult.builder().build().map(result))
                  .collect(Collectors.toList()))
          .build();
    else return this.toBuilder().build();
  }

  @Override
  protected String getResultObjectClassName() {
    return DryRunTransactionResults.class.getName();
  }
}
