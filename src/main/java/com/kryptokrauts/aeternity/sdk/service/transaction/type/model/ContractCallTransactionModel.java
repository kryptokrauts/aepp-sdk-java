package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.api.rxjava.InternalApi;
import com.kryptokrauts.aeternity.generated.model.ContractCallTx;
import com.kryptokrauts.aeternity.generated.model.Tx;
import com.kryptokrauts.aeternity.sdk.annotations.Mandatory;
import com.kryptokrauts.aeternity.sdk.constants.BaseConstants;
import com.kryptokrauts.aeternity.sdk.constants.VirtualMachine;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.ContractCallTransaction;
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
  @Default private BigInteger gasLimit = BaseConstants.CONTRACT_DEFAULT_GAS_LIMIT;
  @Default private BigInteger gasPrice = BaseConstants.MINIMAL_GAS_PRICE;
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
    contractCallTx.setGas(gasLimit);
    contractCallTx.setGasPrice(gasPrice);
    contractCallTx.setNonce(nonce);
    contractCallTx.setTtl(ttl);

    return contractCallTx;
  }

  @Override
  public Function<Tx, ContractCallTransactionModel> getApiToModelFunction() {
    return (tx) ->
        this.toBuilder()
            .amount(tx.getAmount())
            .callData(tx.getCallData())
            .callerId(tx.getCallerId())
            .contractId(tx.getContractId())
            .fee(tx.getFee())
            .gasLimit(tx.getGas())
            .gasPrice(tx.getGasPrice())
            .ttl(tx.getTtl())
            .virtualMachine(VirtualMachine.getVirtualMachine(tx.getAbiVersion()))
            .nonce(tx.getNonce())
            .build();
  }

  @Override
  public void validateInput() {
    // TODO Auto-generated method stub
  }

  @Override
  public AbstractTransaction<?> buildTransaction(ExternalApi externalApi, InternalApi internalApi) {
    return ContractCallTransaction.builder()
        .externalApi(externalApi)
        .internalApi(internalApi)
        .model(this)
        .build();
  }
}
