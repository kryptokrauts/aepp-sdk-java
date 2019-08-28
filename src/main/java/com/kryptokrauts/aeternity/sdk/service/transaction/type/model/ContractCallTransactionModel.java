package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.ContractCallTx;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.ContractCallTransaction;
import com.kryptokrauts.sophia.compiler.generated.api.rxjava.DefaultApi;
import java.math.BigInteger;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ContractCallTransactionModel extends AbstractTransactionModel<ContractCallTx> {

  @NonNull private BigInteger abiVersion;
  @Default private BigInteger amount = BigInteger.ZERO;
  @NonNull private String callData;
  @NonNull private String callerId;
  @NonNull private String contractId;
  @NonNull private BigInteger gas;
  @NonNull private BigInteger gasPrice;
  @NonNull private BigInteger nonce;
  @NonNull private BigInteger ttl;

  @Override
  public ContractCallTx toApiModel() {
    ContractCallTx contractCallTx = new ContractCallTx();
    contractCallTx.setAbiVersion(abiVersion);
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
  public void validateInput() {
    // TODO Auto-generated method stub

  }

  @Override
  public AbstractTransaction<?> buildTransaction(ExternalApi externalApi, DefaultApi compilerApi) {
    return ContractCallTransaction.builder().externalApi(externalApi).model(this).build();
  }
}
