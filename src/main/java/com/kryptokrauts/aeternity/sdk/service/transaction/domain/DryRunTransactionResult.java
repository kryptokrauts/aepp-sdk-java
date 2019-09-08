package com.kryptokrauts.aeternity.sdk.service.transaction.domain;

import com.kryptokrauts.aeternity.generated.model.DryRunResult;
import com.kryptokrauts.aeternity.sdk.domain.GenericResultObject;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
public class DryRunTransactionResult
    extends GenericResultObject<DryRunResult, DryRunTransactionResult> {

  private String type;

  private String result;

  private String reason;

  private DryRunContractCallObjectModel contractCallObject;

  @Override
  protected DryRunTransactionResult map(DryRunResult generatedResultObject) {
    if (generatedResultObject != null)
      return this.toBuilder()
          .type(generatedResultObject.getType())
          .result(generatedResultObject.getResult())
          .reason(generatedResultObject.getReason())
          .contractCallObject(
              DryRunContractCallObjectModel.builder()
                  .build()
                  .map(generatedResultObject.getCallObj()))
          .build();
    else return this.toBuilder().build();
  }

  @Override
  protected String getResultObjectClassName() {
    return DryRunTransactionResult.class.getName();
  }
}
