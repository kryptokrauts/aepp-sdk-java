package com.kryptokrauts.aeternity.sdk.service.transaction.domain;

import com.kryptokrauts.aeternity.generated.model.ContractCallObject;
import com.kryptokrauts.aeternity.generated.model.Event;
import com.kryptokrauts.aeternity.sdk.domain.GenericResultObject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class DryRunContractCallObjectModel
    extends GenericResultObject<ContractCallObject, DryRunContractCallObjectModel> {

  private String callerId;

  private BigInteger callerNonce;

  private BigInteger height;

  private String contractId;

  private BigInteger gasPrice;

  private BigInteger gasUsed;

  @Default private List<Event> log = new ArrayList<>();

  private String returnValue;

  private String returnType;

  @Override
  protected DryRunContractCallObjectModel map(ContractCallObject generatedResultObject) {
    if (generatedResultObject != null)
      return DryRunContractCallObjectModel.builder()
          .callerId(generatedResultObject.getCallerId())
          .callerNonce(generatedResultObject.getCallerNonce())
          .height(generatedResultObject.getHeight())
          .contractId(generatedResultObject.getContractId())
          .gasPrice(generatedResultObject.getGasPrice())
          .gasUsed(generatedResultObject.getGasUsed())
          .build();
    else return DryRunContractCallObjectModel.builder().build();
  }

  @Override
  protected String getResultObjectClassName() {
    return DryRunContractCallObjectModel.class.getName();
  }
}
