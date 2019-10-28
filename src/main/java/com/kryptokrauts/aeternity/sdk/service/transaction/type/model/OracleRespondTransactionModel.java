package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.GenericTx;
import com.kryptokrauts.aeternity.generated.model.OracleRespondTx;
import com.kryptokrauts.aeternity.generated.model.RelativeTTL;
import com.kryptokrauts.aeternity.generated.model.RelativeTTL.TypeEnum;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.OracleRespondTransaction;
import com.kryptokrauts.sophia.compiler.generated.api.rxjava.DefaultApi;
import java.math.BigInteger;
import java.util.function.Function;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@ToString
public class OracleRespondTransactionModel extends AbstractTransactionModel<OracleRespondTx> {

  private String oracleId;
  private String queryId;
  private BigInteger nonce;
  private String response;
  private BigInteger responseTtl;
  private BigInteger ttl;

  @Override
  public OracleRespondTx toApiModel() {
    OracleRespondTx oracleRespondTx = new OracleRespondTx();
    oracleRespondTx.fee(this.fee);
    oracleRespondTx.nonce(this.nonce);
    oracleRespondTx.oracleId(this.oracleId);
    oracleRespondTx.queryId(this.queryId);
    oracleRespondTx.response(this.response);
    oracleRespondTx.responseTtl(new RelativeTTL().type(TypeEnum.DELTA).value(responseTtl));
    oracleRespondTx.ttl(this.ttl);
    return oracleRespondTx;
  }

  @Override
  public Function<GenericTx, OracleRespondTransactionModel> getApiToModelFunction() {
    return (tx) -> {
      OracleRespondTx castedTx = (OracleRespondTx) tx;
      return this.toBuilder()
          .oracleId(castedTx.getOracleId())
          .nonce(castedTx.getNonce())
          .fee(castedTx.getFee())
          .queryId(castedTx.getQueryId())
          .response(castedTx.getResponse())
          .responseTtl(castedTx.getResponseTtl().getValue())
          .ttl(castedTx.getTtl())
          .build();
    };
  }

  @Override
  public void validateInput() {
    // TODO Auto-generated method stub

  }

  @Override
  public AbstractTransaction<?> buildTransaction(ExternalApi externalApi, DefaultApi compilerApi) {
    return OracleRespondTransaction.builder().externalApi(externalApi).model(this).build();
  }
}
