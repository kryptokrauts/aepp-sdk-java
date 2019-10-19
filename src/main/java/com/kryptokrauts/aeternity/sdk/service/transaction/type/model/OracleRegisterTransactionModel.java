package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.GenericTx;
import com.kryptokrauts.aeternity.generated.model.OracleRegisterTx;
import com.kryptokrauts.aeternity.generated.model.TTL;
import com.kryptokrauts.aeternity.sdk.service.oracle.domain.OracleTTLType;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.OracleRegisterTransaction;
import com.kryptokrauts.sophia.compiler.generated.api.rxjava.DefaultApi;
import java.math.BigInteger;
import java.util.function.Function;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@ToString
public class OracleRegisterTransactionModel extends AbstractTransactionModel<OracleRegisterTx> {

  private String accountId;
  private BigInteger abiVersion;
  private BigInteger nonce;
  private BigInteger oracleTtl;
  private OracleTTLType oracleTtlType;
  private BigInteger queryFee;
  private String queryFormat;
  private String responseFormat;
  private BigInteger ttl;

  @Override
  public OracleRegisterTx toApiModel() {
    OracleRegisterTx oracleRegisterTx = new OracleRegisterTx();
    oracleRegisterTx.setAbiVersion(this.abiVersion);
    oracleRegisterTx.setAccountId(this.accountId);
    oracleRegisterTx.setFee(this.fee);
    oracleRegisterTx.setNonce(this.nonce);
    oracleRegisterTx.setOracleTtl(new TTL().type(oracleTtlType.toGeneratedEnum()).value(oracleTtl));
    oracleRegisterTx.setQueryFee(this.queryFee);
    oracleRegisterTx.setQueryFormat(this.queryFormat);
    oracleRegisterTx.setResponseFormat(this.responseFormat);
    oracleRegisterTx.setTtl(this.ttl);
    return oracleRegisterTx;
  }

  @Override
  public Function<GenericTx, OracleRegisterTransactionModel> getApiToModelFunction() {
    return (tx) -> {
      OracleRegisterTx castedTx = (OracleRegisterTx) tx;
      return this.toBuilder()
          .abiVersion(castedTx.getAbiVersion())
          .accountId(castedTx.getAccountId())
          .fee(castedTx.getFee())
          .oracleTtl(castedTx.getOracleTtl().getValue())
          .queryFee(castedTx.getQueryFee())
          .queryFormat(castedTx.getQueryFormat())
          .responseFormat(castedTx.getResponseFormat())
          .nonce(castedTx.getNonce())
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
    return OracleRegisterTransaction.builder().externalApi(externalApi).model(this).build();
  }
}
