package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.GenericTx;
import com.kryptokrauts.aeternity.generated.model.NameClaimTx;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.NameClaimTransaction;
import com.kryptokrauts.aeternity.sdk.util.ValidationUtil;
import com.kryptokrauts.sophia.compiler.generated.api.rxjava.DefaultApi;
import java.math.BigInteger;
import java.util.function.Function;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
public class NameClaimTransactionModel extends AbstractTransactionModel<NameClaimTx> {

  private String accountId;
  private BigInteger nonce;
  private String name;
  private BigInteger nameSalt;
  private BigInteger ttl;

  @Override
  public NameClaimTx toApiModel() {
    NameClaimTx nameClaimTx = new NameClaimTx();
    nameClaimTx.setAccountId(this.accountId);
    nameClaimTx.setNonce(this.nonce);
    nameClaimTx.setName(this.name);
    nameClaimTx.setNameSalt(this.nameSalt);
    nameClaimTx.setFee(this.fee);
    nameClaimTx.setTtl(this.ttl);
    return nameClaimTx;
  }

  @Override
  public Function<GenericTx, NameClaimTransactionModel> getApiToModelFunction() {
    return (tx) -> {
      NameClaimTx castedTx = (NameClaimTx) tx;
      return this.toBuilder()
          .accountId(castedTx.getAccountId())
          .fee(castedTx.getFee())
          .nonce(castedTx.getNonce())
          .name(castedTx.getName())
          .nameSalt(castedTx.getNameSalt())
          .ttl(castedTx.getTtl())
          .build();
    };
  }

  @Override
  public void validateInput() {
    ValidationUtil.checkNamespace(this.name);
  }

  @Override
  public AbstractTransaction<?> buildTransaction(ExternalApi externalApi, DefaultApi compilerApi) {
    return NameClaimTransaction.builder().externalApi(externalApi).model(this).build();
  }
}
