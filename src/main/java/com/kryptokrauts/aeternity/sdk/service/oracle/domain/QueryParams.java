package com.kryptokrauts.aeternity.sdk.service.oracle.domain;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;

@Builder
@Getter
public class QueryParams {
  /** Last query id in previous page */
  @Default private String from = null;
  /** Max number of oracle queries to receive */
  @Default private Integer limit = 50;
  /** The type of a query: open, closed or all */
  @Default private QueryType queryType = QueryType.OPEN;
}
