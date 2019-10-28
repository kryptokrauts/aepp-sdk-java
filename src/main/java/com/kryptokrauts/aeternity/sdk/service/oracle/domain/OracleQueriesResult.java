package com.kryptokrauts.aeternity.sdk.service.oracle.domain;

import com.kryptokrauts.aeternity.generated.model.OracleQueries;
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
public class OracleQueriesResult extends GenericResultObject<OracleQueries, OracleQueriesResult> {

  @NonNull @Default private List<OracleQueryResult> queryResults = new ArrayList<>();

  @Override
  protected OracleQueriesResult map(OracleQueries generatedResultObject) {
    if (generatedResultObject != null)
      return this.toBuilder()
          .queryResults(
              generatedResultObject.getOracleQueries().stream()
                  .map(result -> OracleQueryResult.builder().build().map(result))
                  .collect(Collectors.toList()))
          .build();
    else return this.toBuilder().build();
  }

  @Override
  protected String getResultObjectClassName() {
    return this.getClass().getName();
  }
}
