package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.api.rxjava.InternalApi;
import com.kryptokrauts.aeternity.generated.model.GAAttachTx;
import com.kryptokrauts.aeternity.generated.model.Tx;
import com.kryptokrauts.aeternity.sdk.annotations.Mandatory;
import com.kryptokrauts.aeternity.sdk.constants.BaseConstants;
import com.kryptokrauts.aeternity.sdk.constants.VirtualMachine;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.GeneralizedAccountsAttachTransaction;
import java.math.BigInteger;
import java.util.function.Function;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@ToString
public class GeneralizedAccountsAttachTransactionModel
    extends AbstractTransactionModel<GAAttachTx> {

  @Mandatory private String authFun;
  @Mandatory private String callData;
  @Mandatory private String code;
  @Default private BigInteger gasLimit = BaseConstants.CONTRACT_DEFAULT_GAS_LIMIT;
  @Default private BigInteger gasPrice = BaseConstants.MINIMAL_GAS_PRICE;
  @Mandatory private BigInteger nonce;
  @Mandatory private String ownerId;
  @Default private BigInteger ttl = BigInteger.ZERO;
  @Default private VirtualMachine virtualMachine = VirtualMachine.FATE;

  @Override
  public GAAttachTx toApiModel() {
    GAAttachTx gaAttachTx = new GAAttachTx();
    gaAttachTx.abiVersion(virtualMachine.getAbiVersion());
    gaAttachTx.authFun(authFun);
    gaAttachTx.callData(callData);
    gaAttachTx.code(code);
    gaAttachTx.fee(fee);
    gaAttachTx.gas(gasLimit);
    gaAttachTx.gasPrice(gasPrice);
    gaAttachTx.nonce(nonce);
    gaAttachTx.ownerId(ownerId);
    gaAttachTx.ttl(ttl);
    gaAttachTx.vmVersion(virtualMachine.getVmVersion());
    return gaAttachTx;
  }

  @Override
  public void validateInput() {}

  @Override
  public Function<Tx, GeneralizedAccountsAttachTransactionModel> getApiToModelFunction() {
    return (tx) ->
        this.toBuilder()
            .authFun(tx.getAuthFun())
            .callData(tx.getCallData())
            .code(tx.getCode())
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
  public AbstractTransaction<?> buildTransaction(ExternalApi externalApi, InternalApi internalApi) {
    return GeneralizedAccountsAttachTransaction.builder()
        .externalApi(externalApi)
        .model(this)
        .build();
  }
}
