package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.GAAttachTx;
import com.kryptokrauts.aeternity.generated.model.GenericTx;
import com.kryptokrauts.aeternity.sdk.annotations.Mandatory;
import com.kryptokrauts.aeternity.sdk.constants.VirtualMachine;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.GeneralizedAccountsAttachTransaction;
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
public class GeneralizedAccountsAttachTransactionModel
    extends AbstractTransactionModel<GAAttachTx> {

  @Mandatory private String authFun;
  @Mandatory private String callData;
  @Mandatory private String code;
  @Mandatory private BigInteger gas;
  @Mandatory private BigInteger gasPrice;
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
    gaAttachTx.gas(gas);
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
  public AbstractTransaction<?> buildTransaction(ExternalApi externalApi, DefaultApi compilerApi) {
    return GeneralizedAccountsAttachTransaction.builder()
        .externalApi(externalApi)
        .model(this)
        .build();
  }

  @Override
  public Function<GenericTx, GeneralizedAccountsAttachTransactionModel> getApiToModelFunction() {
    return (tx) -> {
      GAAttachTx castedTx = (GAAttachTx) tx;
      return this.toBuilder()
          .authFun(castedTx.getAuthFun())
          .callData(castedTx.getCallData())
          .code(castedTx.getCode())
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
}
