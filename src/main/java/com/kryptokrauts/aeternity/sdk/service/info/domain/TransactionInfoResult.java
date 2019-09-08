package com.kryptokrauts.aeternity.sdk.service.info.domain;

import com.kryptokrauts.aeternity.generated.model.TxInfoObject;
import com.kryptokrauts.aeternity.sdk.domain.GenericResultObject;
import com.kryptokrauts.aeternity.sdk.service.transaction.domain.ContractCallObjectModel;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@ToString
public class TransactionInfoResult
    extends GenericResultObject<TxInfoObject, TransactionInfoResult> {

  private ContractCallObjectModel callInfo;

  private GAObjectModel gaInfo;

  private String txInfo;

  @Override
  protected TransactionInfoResult map(TxInfoObject generatedResultObject) {
    if (generatedResultObject != null)
      return this.toBuilder()
          .txInfo(generatedResultObject.getTxInfo())
          .callInfo(
              ContractCallObjectModel.builder().build().map(generatedResultObject.getCallInfo()))
          .gaInfo(GAObjectModel.builder().build().map(generatedResultObject.getGaInfo()))
          .build();
    else return this.toBuilder().build();
  }

  @Override
  protected String getResultObjectClassName() {
    return this.getClass().getName();
  }
}
