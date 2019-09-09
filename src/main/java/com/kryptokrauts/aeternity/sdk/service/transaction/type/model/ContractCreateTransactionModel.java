package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.ContractCreateTx;
import com.kryptokrauts.aeternity.generated.model.GenericTx;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.ContractCreateTransaction;
import com.kryptokrauts.sophia.compiler.generated.api.rxjava.DefaultApi;
import java.math.BigInteger;
import java.util.function.Function;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
public class ContractCreateTransactionModel extends AbstractTransactionModel<ContractCreateTx> {

  private BigInteger abiVersion;
  private BigInteger amount;
  private String callData;
  private String contractByteCode;
  private BigInteger deposit;
  private BigInteger gas;
  private BigInteger gasPrice;
  private BigInteger nonce;
  private String ownerId;
  private BigInteger ttl;
  private BigInteger vmVersion;

  @Override
  public ContractCreateTx toApiModel() {
    ContractCreateTx contractCreateTx = new ContractCreateTx();
    contractCreateTx.setAbiVersion(abiVersion);
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
    contractCreateTx.setVmVersion(vmVersion);

    return contractCreateTx;
  }

  @Override
  public Function<GenericTx, ContractCreateTransactionModel> getApiToModelFunction() {
    return (tx) -> {
      ContractCreateTx castedTx = (ContractCreateTx) tx;
      return this.toBuilder()
          .abiVersion(castedTx.getAbiVersion())
          .amount(castedTx.getAmount())
          .callData(castedTx.getCallData())
          .contractByteCode(castedTx.getCode())
          .deposit(castedTx.getDeposit())
          .fee(castedTx.getFee())
          .gas(castedTx.getGas())
          .ownerId(castedTx.getOwnerId())
          .vmVersion(castedTx.getVmVersion())
          .gasPrice(castedTx.getGasPrice())
          .ttl(castedTx.getTtl())
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
    return ContractCreateTransaction.builder().externalApi(externalApi).model(this).build();
  }
}
