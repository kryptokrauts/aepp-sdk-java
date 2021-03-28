package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.ContractCallTx;
import com.kryptokrauts.aeternity.generated.model.GenericTx;
import com.kryptokrauts.aeternity.sdk.annotations.Mandatory;
import com.kryptokrauts.aeternity.sdk.constants.VirtualMachine;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.ContractCallTransaction;
import com.kryptokrauts.sophia.compiler.generated.api.rxjava.DefaultApi;
import java.math.BigInteger;
import java.util.function.Function;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@ToString
public class ContractCallTransactionModel extends AbstractTransactionModel<ContractCallTx> {

  @Mandatory @Default private BigInteger amount = BigInteger.ZERO;
  @Mandatory private String callData;
  @Mandatory private String callerId;
  @Mandatory private String contractId;
  @Mandatory private BigInteger gas;
  @Mandatory private BigInteger gasPrice;
  @Mandatory private BigInteger nonce;
  @Default private BigInteger ttl = BigInteger.ZERO;
  @Default private VirtualMachine virtualMachine = VirtualMachine.FATE;

  @Override
  public ContractCallTx toApiModel() {
    ContractCallTx contractCallTx = new ContractCallTx();
    contractCallTx.setAbiVersion(virtualMachine.getAbiVersion());
    contractCallTx.setAmount(amount);
    contractCallTx.setCallData(callData);
    contractCallTx.setCallerId(callerId);
    contractCallTx.setContractId(contractId);
    contractCallTx.setFee(fee);
    contractCallTx.setGas(gas);
    contractCallTx.setGasPrice(gasPrice);
    contractCallTx.setNonce(nonce);
    contractCallTx.setTtl(ttl);

    return contractCallTx;
  }

  @Override
  public Function<GenericTx, ContractCallTransactionModel> getApiToModelFunction() {
    return (tx) -> {
      ContractCallTx castedTx = (ContractCallTx) tx;
      return this.toBuilder()
          .amount(castedTx.getAmount())
          .callData(castedTx.getCallData())
          .callerId(castedTx.getCallerId())
          .contractId(castedTx.getContractId())
          .fee(castedTx.getFee())
          .gas(castedTx.getGas())
          .gasPrice(castedTx.getGasPrice())
          .ttl(castedTx.getTtl())
          .virtualMachine(VirtualMachine.getVirtualMachine(castedTx.getAbiVersion()))
          .nonce(castedTx.getNonce())
          .build();
    };
  }

  @Override
  public void validateInput() {
    // TODO Auto-generated method stub

  }

  @Override
  public AbstractTransaction<?> buildTransaction(ExternalApi externalApi, DefaultApi compilerApi) {
    return ContractCallTransaction.builder().externalApi(externalApi).model(this).build();
  }
}
