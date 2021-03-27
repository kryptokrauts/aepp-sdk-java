package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.GAMetaTx;
import com.kryptokrauts.aeternity.generated.model.GenericTx;
import com.kryptokrauts.aeternity.sdk.annotations.Mandatory;
import com.kryptokrauts.aeternity.sdk.constants.BaseConstants;
import com.kryptokrauts.aeternity.sdk.constants.VirtualMachine;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.GeneralizedAccountsMetaTransaction;
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
public class GeneralizedAccountsMetaTransactionModel extends AbstractTransactionModel<GAMetaTx> {

  @Mandatory private String gaId;
  @Mandatory private String authData;
  @Default private BigInteger gas = BigInteger.valueOf(50000);
  @Default private BigInteger gasPrice = BigInteger.valueOf(BaseConstants.MINIMAL_GAS_PRICE);
  @Mandatory private String tx;
  @Mandatory private VirtualMachine virtualMachine;

  @Override
  public GAMetaTx toApiModel() {
    GAMetaTx gaMetaTx = new GAMetaTx();
    gaMetaTx.gaId(gaId);
    gaMetaTx.authData(authData);
    gaMetaTx.abiVersion(virtualMachine.getAbiVersion());
    gaMetaTx.fee(fee);
    gaMetaTx.gas(gas);
    gaMetaTx.gasPrice(gasPrice);
    // TODO map tx?
    return gaMetaTx;
  }

  @Override
  public void validateInput() {}

  @Override
  public AbstractTransaction<?> buildTransaction(ExternalApi externalApi, DefaultApi compilerApi) {
    return GeneralizedAccountsMetaTransaction.builder()
        .externalApi(externalApi)
        .model(this)
        .build();
  }

  @Override
  public Function<GenericTx, GeneralizedAccountsMetaTransactionModel> getApiToModelFunction() {
    return (tx) -> {
      GAMetaTx castedTx = (GAMetaTx) tx;
      return this.toBuilder()
          .gaId(castedTx.getGaId())
          .authData(castedTx.getAuthData())
          .fee(castedTx.getFee())
          .gas(castedTx.getGas())
          .gasPrice(castedTx.getGasPrice())
          .virtualMachine(VirtualMachine.getVirtualMachine(castedTx.getAbiVersion()))
          // TODO map tx?
          .build();
    };
  }
}
