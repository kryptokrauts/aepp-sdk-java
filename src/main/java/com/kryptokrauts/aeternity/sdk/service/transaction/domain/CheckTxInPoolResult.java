package com.kryptokrauts.aeternity.sdk.service.transaction.domain;

import com.kryptokrauts.aeternity.generated.model.CheckTxInPoolResponse;
import com.kryptokrauts.aeternity.sdk.domain.GenericResultObject;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@ToString
public class CheckTxInPoolResult
    extends GenericResultObject<CheckTxInPoolResponse, CheckTxInPoolResult> {

  private String status;

  @Override
  protected CheckTxInPoolResult map(CheckTxInPoolResponse generatedResultObject) {
    if (generatedResultObject != null)
      return this.toBuilder().status(generatedResultObject.getStatus()).build();
    else return this.toBuilder().build();
  }

  @Override
  protected String getResultObjectClassName() {
    return CheckTxInPoolResult.class.getName();
  }
}
