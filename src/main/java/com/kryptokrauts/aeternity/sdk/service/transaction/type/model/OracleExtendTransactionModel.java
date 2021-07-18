package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.api.rxjava.InternalApi;
import com.kryptokrauts.aeternity.generated.model.OracleExtendTx;
import com.kryptokrauts.aeternity.generated.model.TTL;
import com.kryptokrauts.aeternity.generated.model.TTL.TypeEnum;
import com.kryptokrauts.aeternity.generated.model.Tx;
import com.kryptokrauts.aeternity.sdk.annotations.Mandatory;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.OracleExtendTransaction;
import java.math.BigInteger;
import java.util.function.Function;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@ToString
public class OracleExtendTransactionModel extends AbstractTransactionModel<OracleExtendTx> {

  @Mandatory private BigInteger nonce;
  @Mandatory private String oracleId;
  @Mandatory private BigInteger relativeTtl;
  @Default private BigInteger ttl = BigInteger.ZERO;

  @Override
  public OracleExtendTx toApiModel() {
    OracleExtendTx oracleExtendTx = new OracleExtendTx();
    oracleExtendTx.setFee(this.fee);
    oracleExtendTx.setNonce(this.nonce);
    oracleExtendTx.setOracleId(this.oracleId);
    oracleExtendTx.setOracleTtl(new TTL().type(TypeEnum.DELTA).value(relativeTtl));
    oracleExtendTx.setTtl(this.ttl);
    return oracleExtendTx;
  }

  @Override
  public Function<Tx, OracleExtendTransactionModel> getApiToModelFunction() {
    return (tx) ->
        this.toBuilder()
            .oracleId(tx.getOracleId())
            .nonce(tx.getNonce())
            .relativeTtl(tx.getOracleTtl().getValue())
            .ttl(tx.getTtl())
            .build();
  }

  @Override
  public void validateInput() {
    // TODO Auto-generated method stub
  }

  @Override
  public AbstractTransaction<?> buildTransaction(ExternalApi externalApi, InternalApi internalApi) {
    return OracleExtendTransaction.builder()
        .externalApi(externalApi)
        .internalApi(internalApi)
        .model(this)
        .build();
  }
}
