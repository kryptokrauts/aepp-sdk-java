package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.ContractCreateTx;
import com.kryptokrauts.aeternity.generated.model.GenericTx;
import com.kryptokrauts.aeternity.sdk.annotations.Mandatory;
import com.kryptokrauts.aeternity.sdk.constants.VirtualMachine;
import com.kryptokrauts.aeternity.sdk.exception.InvalidParameterException;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.ContractCreateTransaction;
import com.kryptokrauts.sophia.compiler.generated.api.rxjava.DefaultApi;
import java.math.BigInteger;
import java.util.function.Function;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@ToString
public class ContractCreateTransactionModel extends AbstractTransactionModel<ContractCreateTx> {

  @Mandatory private BigInteger amount;
  @Mandatory private String callData;
  @Mandatory private String contractByteCode;
  private BigInteger deposit;
  @Mandatory private BigInteger gas;
  @Mandatory private BigInteger gasPrice;
  @Mandatory private BigInteger nonce;
  @Mandatory private String ownerId;
  @Mandatory private BigInteger ttl;
  @Mandatory private VirtualMachine virtualMachine;

  @Override
  public ContractCreateTx toApiModel() {
    ContractCreateTx contractCreateTx = new ContractCreateTx();
    contractCreateTx.setAbiVersion(virtualMachine.getAbiVersion());
    contractCreateTx.setAmount(amount);
    contractCreateTx.setCallData(callData);
    contractCreateTx.setCode(contractByteCode);
    contractCreateTx.setDeposit(deposit);
    contractCreateTx.setFee(fee);
    contractCreateTx.setGas(gas);
    contractCreateTx.setGasPrice(gasPrice);
    contractCreateTx.setNonce(nonce);
    contractCreateTx.setOwnerId(ownerId);
    contractCreateTx.setTtl(ttl);
    contractCreateTx.setVmVersion(virtualMachine.getVmVersion());

    return contractCreateTx;
  }

  @Override
  public Function<GenericTx, ContractCreateTransactionModel> getApiToModelFunction() {
    return (tx) -> {
      ContractCreateTx castedTx = (ContractCreateTx) tx;
      return this.toBuilder()
          .amount(castedTx.getAmount())
          .callData(castedTx.getCallData())
          .contractByteCode(castedTx.getCode())
          .deposit(castedTx.getDeposit())
          .fee(castedTx.getFee())
          .gas(castedTx.getGas())
          .ownerId(castedTx.getOwnerId())
          .gasPrice(castedTx.getGasPrice())
          .ttl(castedTx.getTtl())
          .nonce(castedTx.getNonce())
          .virtualMachine(VirtualMachine.getVirtualMachine(castedTx.getAbiVersion()))
          .build();
    };
  }

  @Override
  public void validateInput() {
    if (!VirtualMachine.FATE.equals(this.virtualMachine)) {
      throw new InvalidParameterException(
          "AEVM cannot be used for the creation of contracts anymore.");
    }
  }

  @Override
  public AbstractTransaction<?> buildTransaction(ExternalApi externalApi, DefaultApi compilerApi) {
    return ContractCreateTransaction.builder().externalApi(externalApi).model(this).build();
  }
}
