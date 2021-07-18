package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.api.rxjava.InternalApi;
import com.kryptokrauts.aeternity.generated.model.OracleQueryTx;
import com.kryptokrauts.aeternity.generated.model.TTL;
import com.kryptokrauts.aeternity.generated.model.TTL.TypeEnum;
import com.kryptokrauts.aeternity.generated.model.Tx;
import com.kryptokrauts.aeternity.sdk.annotations.Mandatory;
import com.kryptokrauts.aeternity.sdk.service.oracle.domain.OracleTTLType;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.OracleQueryTransaction;
import java.math.BigInteger;
import java.util.function.Function;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@ToString
public class OracleQueryTransactionModel extends AbstractTransactionModel<OracleQueryTx> {

  @Mandatory private String senderId;
  @Mandatory private String oracleId;
  @Mandatory private BigInteger nonce;
  @Mandatory private String query;
  @Mandatory private BigInteger queryFee;
  @Mandatory private BigInteger queryTtl;
  @Mandatory private OracleTTLType queryTtlType;
  @Mandatory private BigInteger responseTtl;
  @Default private BigInteger ttl = BigInteger.ZERO;

  @Override
  public OracleQueryTx toApiModel() {
    OracleQueryTx oracleQueryTx = new OracleQueryTx();
    oracleQueryTx.senderId(this.senderId);
    oracleQueryTx.oracleId(this.oracleId);
    oracleQueryTx.fee(this.fee);
    oracleQueryTx.nonce(this.nonce);
    oracleQueryTx.query(this.query);
    oracleQueryTx.queryFee(this.queryFee);
    oracleQueryTx.queryTtl(new TTL().type(queryTtlType.toGeneratedEnum()).value(queryTtl));
    oracleQueryTx.responseTtl(new TTL().type(TypeEnum.DELTA).value(responseTtl));
    return oracleQueryTx;
  }

  @Override
  public Function<Tx, OracleQueryTransactionModel> getApiToModelFunction() {
    return (tx) ->
        this.toBuilder()
            .oracleId(tx.getOracleId())
            .nonce(tx.getNonce())
            .senderId(tx.getSenderId())
            .query(tx.getQuery())
            .queryFee(tx.getQueryFee())
            .queryTtl(tx.getQueryTtl().getValue())
            .queryTtlType(OracleTTLType.fromTypeEnum(tx.getQueryTtl()))
            .responseTtl(tx.getResponseTtl().getValue())
            .ttl(tx.getTtl())
            .build();
  }

  @Override
  public void validateInput() {
    // nothing to validate
  }

  @Override
  public AbstractTransaction<?> buildTransaction(ExternalApi externalApi, InternalApi internalApi) {
    return OracleQueryTransaction.builder()
        .externalApi(externalApi)
        .internalApi(internalApi)
        .model(this)
        .build();
  }
}
