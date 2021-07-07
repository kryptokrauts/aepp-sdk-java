package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.GenericTx;
import com.kryptokrauts.aeternity.generated.model.OracleExtendTx;
import com.kryptokrauts.aeternity.generated.model.RelativeTTL;
import com.kryptokrauts.aeternity.generated.model.RelativeTTL.TypeEnum;
import com.kryptokrauts.aeternity.sdk.annotations.Mandatory;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.OracleExtendTransaction;
import com.kryptokrauts.sophia.compiler.generated.api.rxjava.DefaultApi;
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
  @Mandatory private BigInteger oracleRelativeTtl;
  @Default private BigInteger ttl = BigInteger.ZERO;

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
  public Function<GenericTx, OracleExtendTransactionModel> getApiToModelFunction() {
    return (tx) -> {
      OracleExtendTx castedTx = (OracleExtendTx) tx;
      return this.toBuilder()
          .oracleId(castedTx.getOracleId())
          .nonce(castedTx.getNonce())
          .oracleRelativeTtl(castedTx.getOracleTtl().getValue())
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
    return OracleExtendTransaction.builder().externalApi(externalApi).model(this).build();
  }
}
