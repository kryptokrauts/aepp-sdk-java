package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.OracleRegisterTx;
import com.kryptokrauts.aeternity.generated.model.TTL;
import com.kryptokrauts.aeternity.sdk.service.oracle.domain.OracleTTLType;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.OracleRegisterTransaction;
import com.kryptokrauts.sophia.compiler.generated.api.rxjava.DefaultApi;
import java.math.BigInteger;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class OracleRegisterTransactionModel extends AbstractTransactionModel<OracleRegisterTx> {

  @NonNull private String accountId;
  @NonNull private BigInteger abiVersion;
  @NonNull private BigInteger nonce;
  @NonNull private BigInteger oracleTtl;
  @NonNull private OracleTTLType oracleTtlType;
  @NonNull private BigInteger queryFee;
  @NonNull private String queryFormat;
  @NonNull private String responseFormat;
  @NonNull private BigInteger ttl;

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
  public void validateInput() {
    // TODO Auto-generated method stub

  }

  @Override
  public AbstractTransaction<?> buildTransaction(ExternalApi externalApi, DefaultApi compilerApi) {
    return OracleRegisterTransaction.builder().externalApi(externalApi).model(this).build();
  }
}
