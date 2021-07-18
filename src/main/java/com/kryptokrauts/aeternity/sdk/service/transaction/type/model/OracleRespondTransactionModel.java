package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.api.rxjava.InternalApi;
import com.kryptokrauts.aeternity.generated.model.OracleRespondTx;
import com.kryptokrauts.aeternity.generated.model.TTL;
import com.kryptokrauts.aeternity.generated.model.TTL.TypeEnum;
import com.kryptokrauts.aeternity.generated.model.Tx;
import com.kryptokrauts.aeternity.sdk.annotations.Mandatory;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.OracleRespondTransaction;
import java.math.BigInteger;
import java.util.function.Function;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@ToString
public class OracleRespondTransactionModel extends AbstractTransactionModel<OracleRespondTx> {

  @Mandatory private String oracleId;
  @Mandatory private String queryId;
  @Mandatory private BigInteger nonce;
  @Mandatory private String response;
  @Mandatory private BigInteger responseTtl;
  @Default private BigInteger ttl = BigInteger.ZERO;

  @Override
  public OracleRespondTx toApiModel() {
    OracleRespondTx oracleRespondTx = new OracleRespondTx();
    oracleRespondTx.fee(this.fee);
    oracleRespondTx.nonce(this.nonce);
    oracleRespondTx.oracleId(this.oracleId);
    oracleRespondTx.queryId(this.queryId);
    oracleRespondTx.response(this.response);
    oracleRespondTx.responseTtl(new TTL().type(TypeEnum.DELTA).value(responseTtl));
    oracleRespondTx.ttl(this.ttl);
    return oracleRespondTx;
  }

  @Override
  public Function<Tx, OracleRespondTransactionModel> getApiToModelFunction() {
    return (tx) ->
        this.toBuilder()
            .oracleId(tx.getOracleId())
            .nonce(tx.getNonce())
            .fee(tx.getFee())
            .queryId(tx.getQueryId())
            .response(tx.getResponse())
            .responseTtl(tx.getResponseTtl().getValue())
            .ttl(tx.getTtl())
            .build();
  }

  @Override
  public void validateInput() {
    // TODO Auto-generated method stub
  }

  @Override
  public AbstractTransaction<?> buildTransaction(ExternalApi externalApi, InternalApi internalApi) {
    return OracleRespondTransaction.builder()
        .externalApi(externalApi)
        .internalApi(internalApi)
        .model(this)
        .build();
  }
}
