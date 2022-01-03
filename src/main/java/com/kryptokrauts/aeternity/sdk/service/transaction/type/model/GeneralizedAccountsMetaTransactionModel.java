package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.api.rxjava.InternalApi;
import com.kryptokrauts.aeternity.generated.model.GAMetaTx;
import com.kryptokrauts.aeternity.generated.model.Tx;
import com.kryptokrauts.aeternity.sdk.annotations.Mandatory;
import com.kryptokrauts.aeternity.sdk.constants.BaseConstants;
import com.kryptokrauts.aeternity.sdk.constants.VirtualMachine;
import com.kryptokrauts.aeternity.sdk.service.info.domain.ApiModelMapper;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.GeneralizedAccountsMetaTransaction;
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
  @Default private BigInteger gasLimit = BaseConstants.CONTRACT_DEFAULT_GAS_LIMIT;
  @Default private BigInteger gasPrice = BaseConstants.MINIMAL_GAS_PRICE;
  @Default private VirtualMachine virtualMachine = VirtualMachine.FATE;
  @Mandatory private AbstractTransactionModel<?> innerTxModel;

  @Override
  public GAMetaTx toApiModel() {
    GAMetaTx gaMetaTx = new GAMetaTx();
    gaMetaTx.gaId(gaId);
    gaMetaTx.authData(authData);
    gaMetaTx.abiVersion(virtualMachine.getAbiVersion());
    gaMetaTx.fee(fee);
    gaMetaTx.gas(gasLimit);
    gaMetaTx.gasPrice(gasPrice);
    /**
     * we cannot map the inner tx because we need a GenericSignedTx here which cannot be produced
     * from the model class without using the {@link TransactionServiceImpl} which is not allowed
     * here
     */
    return gaMetaTx;
  }

  @Override
  public void validateInput() {}

  @Override
  public AbstractTransaction<?> buildTransaction(ExternalApi externalApi, InternalApi internalApi) {
    return GeneralizedAccountsMetaTransaction.builder()
        .externalApi(externalApi)
        .model(this)
        .innerTxRLPEncodedList(
            this.getInnerTxModel()
                .buildTransaction(externalApi, internalApi)
                .createRLPEncodedList())
        .build();
  }

  @Override
  public Function<Tx, GeneralizedAccountsMetaTransactionModel> getApiToModelFunction() {
    return (tx) ->
        this.toBuilder()
            .gaId(tx.getGaId())
            .authData(tx.getAuthData())
            .fee(tx.getFee())
            .gasLimit(tx.getGas())
            .gasPrice(tx.getGasPrice())
            .virtualMachine(VirtualMachine.getVirtualMachine(tx.getAbiVersion()))
            .innerTxModel(ApiModelMapper.mapToTransactionModel(tx.getTx().getTx()))
            .build();
  }

  @Override
  public boolean doSign() {
    return false;
  }
}
