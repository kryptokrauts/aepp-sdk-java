package com.kryptokrauts.aeternity.sdk.service.transaction.domain;

import com.kryptokrauts.aeternity.generated.model.ContractCallObject;
import com.kryptokrauts.aeternity.sdk.domain.GenericResultObject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@ToString
public class ContractCallObjectModel
    extends GenericResultObject<ContractCallObject, ContractCallObjectModel> {

  private String callerId;

  private BigInteger callerNonce;

  private BigInteger height;

  private String contractId;

  private BigInteger gasPrice;

  private BigInteger gasUsed;

  @Default private List<EventModel> log = new ArrayList<>();

  private String returnValue;

  private String returnType;

  @Override
  public ContractCallObjectModel map(ContractCallObject generatedResultObject) {
    if (generatedResultObject != null)
      return this.toBuilder()
          .callerId(generatedResultObject.getCallerId())
          .callerNonce(generatedResultObject.getCallerNonce())
          .height(generatedResultObject.getHeight())
          .contractId(generatedResultObject.getContractId())
          .gasPrice(generatedResultObject.getGasPrice())
          .log(
              generatedResultObject.getLog().stream()
                  .map(event -> EventModel.builder().build().map(event))
                  .collect(Collectors.toList()))
          .returnType(generatedResultObject.getReturnType())
          .returnValue(generatedResultObject.getReturnValue())
          .gasUsed(generatedResultObject.getGasUsed())
          .build();
    else return this.toBuilder().build();
  }

  @Override
  protected String getResultObjectClassName() {
    return ContractCallObjectModel.class.getName();
  }
}
