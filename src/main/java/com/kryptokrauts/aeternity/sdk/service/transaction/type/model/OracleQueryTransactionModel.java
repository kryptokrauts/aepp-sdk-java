package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.GenericTx;
import com.kryptokrauts.aeternity.generated.model.OracleQueryTx;
import com.kryptokrauts.aeternity.generated.model.RelativeTTL;
import com.kryptokrauts.aeternity.generated.model.TTL;
import com.kryptokrauts.aeternity.sdk.annotations.Mandatory;
import com.kryptokrauts.aeternity.sdk.service.oracle.domain.OracleTTLType;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.OracleQueryTransaction;
import com.kryptokrauts.sophia.compiler.generated.api.rxjava.DefaultApi;
import java.math.BigInteger;
import java.util.function.Function;
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
  @Mandatory private BigInteger ttl;

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
    oracleQueryTx.responseTtl(
        new RelativeTTL()
            .type(com.kryptokrauts.aeternity.generated.model.RelativeTTL.TypeEnum.DELTA)
            .value(responseTtl));
    return oracleQueryTx;
  }

  @Override
  public Function<GenericTx, OracleQueryTransactionModel> getApiToModelFunction() {
    return (tx) -> {
      OracleQueryTx castedTx = (OracleQueryTx) tx;
      return this.toBuilder()
          .oracleId(castedTx.getOracleId())
          .nonce(castedTx.getNonce())
          .senderId(castedTx.getSenderId())
          .query(castedTx.getQuery())
          .queryFee(castedTx.getQueryFee())
          .queryTtl(castedTx.getQueryTtl().getValue())
          .queryTtlType(OracleTTLType.fromTypeEnum(castedTx.getQueryTtl()))
          .responseTtl(castedTx.getResponseTtl().getValue())
          .ttl(castedTx.getTtl())
          .build();
    };
  }

  @Override
  public void validateInput() {
    // nothing to validate
  }

  @Override
  public AbstractTransaction<?> buildTransaction(ExternalApi externalApi, DefaultApi compilerApi) {
    return OracleQueryTransaction.builder().externalApi(externalApi).model(this).build();
  }
}
