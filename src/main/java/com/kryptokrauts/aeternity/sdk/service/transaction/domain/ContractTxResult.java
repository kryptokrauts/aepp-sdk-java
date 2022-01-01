package com.kryptokrauts.aeternity.sdk.service.transaction.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class ContractTxResult {
  private String txHash;
  private ContractCallObjectModel callResult;
  private Object decodedValue;
}
