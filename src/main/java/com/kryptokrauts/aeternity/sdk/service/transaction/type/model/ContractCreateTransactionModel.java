package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.api.rxjava.InternalApi;
import com.kryptokrauts.aeternity.generated.model.ContractCreateTx;
import com.kryptokrauts.aeternity.generated.model.Tx;
import com.kryptokrauts.aeternity.sdk.annotations.Mandatory;
import com.kryptokrauts.aeternity.sdk.constants.BaseConstants;
import com.kryptokrauts.aeternity.sdk.constants.VirtualMachine;
import com.kryptokrauts.aeternity.sdk.exception.InvalidParameterException;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.ContractCreateTransaction;
import java.math.BigInteger;
import java.util.function.Function;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@ToString
public class ContractCreateTransactionModel extends AbstractTransactionModel<ContractCreateTx> {

  @Default private BigInteger amount = BigInteger.ZERO;
  @Mandatory private String callData;
  @Mandatory private String contractByteCode;
  @Default BigInteger deposit = BigInteger.ZERO;
  @Default private BigInteger gasLimit = BaseConstants.CONTRACT_DEFAULT_GAS_LIMIT;
  @Default private BigInteger gasPrice = BaseConstants.MINIMAL_GAS_PRICE;
  @Mandatory private BigInteger nonce;
  @Mandatory private String ownerId;
  @Default private BigInteger ttl = BigInteger.ZERO;
  @Default private VirtualMachine virtualMachine = VirtualMachine.FATE;

  public ContractCreateTransactionModel(
      String byteCode,
      String callData,
      String ownerId,
      BigInteger amount,
      BigInteger nonce,
      BigInteger gasLimit,
      BigInteger gasPrice,
      BigInteger ttl) {
    this.contractByteCode = byteCode;
    this.callData = callData;
    this.ownerId = ownerId;
    this.amount = amount != null ? amount : BigInteger.ZERO;
    this.deposit = BigInteger.ZERO; // must be enforced to avoid loss of funds
    this.nonce = nonce;
    this.gasLimit = gasLimit != null ? gasLimit : BaseConstants.CONTRACT_DEFAULT_GAS_LIMIT;
    this.gasPrice = gasPrice != null ? gasPrice : BaseConstants.MINIMAL_GAS_PRICE;
    this.ttl = ttl != null ? ttl : BigInteger.ZERO;
    this.virtualMachine = VirtualMachine.FATE;
  }

  @Override
  public ContractCreateTx toApiModel() {
    ContractCreateTx contractCreateTx = new ContractCreateTx();
    contractCreateTx.setAbiVersion(virtualMachine.getAbiVersion());
    contractCreateTx.setAmount(amount);
    contractCreateTx.setCallData(callData);
    contractCreateTx.setCode(contractByteCode);
    contractCreateTx.setDeposit(deposit);
    contractCreateTx.setFee(fee);
    contractCreateTx.setGas(gasLimit);
    contractCreateTx.setGasPrice(gasPrice);
    contractCreateTx.setNonce(nonce);
    contractCreateTx.setOwnerId(ownerId);
    contractCreateTx.setTtl(ttl);
    contractCreateTx.setVmVersion(virtualMachine.getVmVersion());

    return contractCreateTx;
  }

  @Override
  public Function<Tx, ContractCreateTransactionModel> getApiToModelFunction() {
    return (tx) ->
        this.toBuilder()
            .amount(tx.getAmount())
            .callData(tx.getCallData())
            .contractByteCode(tx.getCode())
            .deposit(tx.getDeposit())
            .fee(tx.getFee())
            .gasLimit(tx.getGas())
            .ownerId(tx.getOwnerId())
            .gasPrice(tx.getGasPrice())
            .ttl(tx.getTtl())
            .nonce(tx.getNonce())
            .virtualMachine(VirtualMachine.getVirtualMachine(tx.getAbiVersion()))
            .build();
  }

  @Override
  public void validateInput() {
    if (BigInteger.ZERO != this.deposit) {
      throw new InvalidParameterException(
          "Deposit for creation contract should be 0 otherwise deposit will be locked forever");
    }
    if (!VirtualMachine.FATE.equals(this.virtualMachine)) {
      throw new InvalidParameterException(
          "AEVM cannot be used for the creation of contracts anymore.");
    }
  }

  @Override
  public AbstractTransaction<?> buildTransaction(ExternalApi externalApi, InternalApi internalApi) {
    return ContractCreateTransaction.builder()
        .externalApi(externalApi)
        .internalApi(internalApi)
        .model(this)
        .build();
  }
}
