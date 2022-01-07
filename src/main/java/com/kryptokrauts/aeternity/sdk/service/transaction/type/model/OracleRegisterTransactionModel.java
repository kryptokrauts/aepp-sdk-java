package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.api.rxjava.InternalApi;
import com.kryptokrauts.aeternity.generated.model.OracleRegisterTx;
import com.kryptokrauts.aeternity.generated.model.TTL;
import com.kryptokrauts.aeternity.generated.model.Tx;
import com.kryptokrauts.aeternity.sdk.annotations.Mandatory;
import com.kryptokrauts.aeternity.sdk.service.oracle.domain.OracleTTLType;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.OracleRegisterTransaction;
import java.math.BigInteger;
import java.util.function.Function;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@ToString
public class OracleRegisterTransactionModel extends AbstractTransactionModel<OracleRegisterTx> {

  @Mandatory private String accountId;
  @Default private BigInteger abiVersion = BigInteger.ZERO;
  @Mandatory private BigInteger nonce;
  @Mandatory private BigInteger oracleTtl;
  @Mandatory private OracleTTLType oracleTtlType;
  @Mandatory private BigInteger queryFee;
  @Mandatory private String queryFormat;
  @Mandatory private String responseFormat;
  @Default private BigInteger ttl = BigInteger.ZERO;

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
  public Function<Tx, OracleRegisterTransactionModel> getApiToModelFunction() {
    return (tx) ->
        this.toBuilder()
            .abiVersion(tx.getAbiVersion())
            .accountId(tx.getAccountId())
            .fee(tx.getFee())
            .oracleTtl(tx.getOracleTtl().getValue())
            .queryFee(tx.getQueryFee())
            .queryFormat(tx.getQueryFormat())
            .responseFormat(tx.getResponseFormat())
            .nonce(tx.getNonce())
            .ttl(tx.getTtl())
            .build();
  }

  @Override
  public void validateInput() {
    // TODO Auto-generated method stub
  }

  @Override
  public AbstractTransaction<?> buildTransaction(ExternalApi externalApi, InternalApi internalApi) {
    return OracleRegisterTransaction.builder()
        .externalApi(externalApi)
        .internalApi(internalApi)
        .model(this)
        .build();
  }
}
