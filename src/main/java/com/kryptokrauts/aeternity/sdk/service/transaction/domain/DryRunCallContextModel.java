package com.kryptokrauts.aeternity.sdk.service.transaction.domain;

import com.kryptokrauts.aeternity.generated.model.DryRunCallContext;
import com.kryptokrauts.aeternity.sdk.domain.GenericInputObject;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class DryRunCallContextModel extends GenericInputObject<DryRunCallContext> {

  private String txHash;

  private Boolean stateful;

  @Override
  public DryRunCallContext mapToModel() {
    return new DryRunCallContext().txHash(txHash).stateful(stateful);
  }

  @Override
  protected void validate() {}
}
