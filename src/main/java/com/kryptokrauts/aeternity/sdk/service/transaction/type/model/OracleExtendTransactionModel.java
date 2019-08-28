package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.OracleExtendTx;
import com.kryptokrauts.aeternity.generated.model.RelativeTTL;
import com.kryptokrauts.aeternity.generated.model.RelativeTTL.TypeEnum;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.OracleExtendTransaction;
import com.kryptokrauts.sophia.compiler.generated.api.rxjava.DefaultApi;
import java.math.BigInteger;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class OracleExtendTransactionModel extends AbstractTransactionModel<OracleExtendTx> {

  @NonNull private BigInteger nonce;
  @NonNull private String oracleId;
  @NonNull private BigInteger oracleRelativeTtl;
  @NonNull private BigInteger ttl;

  @Override
  public OracleExtendTx toApiModel() {
    OracleExtendTx oracleExtendTx = new OracleExtendTx();
    oracleExtendTx.setFee(this.fee);
    oracleExtendTx.setNonce(this.nonce);
    oracleExtendTx.setOracleId(this.oracleId);
    oracleExtendTx.setOracleTtl(
        new RelativeTTL().type(TypeEnum.DELTA).value(this.oracleRelativeTtl));
    oracleExtendTx.setTtl(this.ttl);
    return oracleExtendTx;
  }

  @Override
  public void validateInput() {
    // TODO Auto-generated method stub

  }

  @Override
  public AbstractTransaction<?> buildTransaction(ExternalApi externalApi, DefaultApi compilerApi) {
    return OracleExtendTransaction.builder().externalApi(externalApi).model(this).build();
  }
}
