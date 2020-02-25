package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.GenericTx;
import com.kryptokrauts.aeternity.generated.model.NamePreclaimTx;
import com.kryptokrauts.aeternity.sdk.annotations.Mandatory;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.NamePreclaimTransaction;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import com.kryptokrauts.aeternity.sdk.util.ValidationUtil;
import com.kryptokrauts.sophia.compiler.generated.api.rxjava.DefaultApi;
import java.math.BigInteger;
import java.util.function.Function;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@ToString
public class NamePreclaimTransactionModel extends AbstractTransactionModel<NamePreclaimTx> {

  @Mandatory private String accountId;
  @Mandatory private String name; // will be used to generate the commitmentId
  @Mandatory private BigInteger salt; // will be used to generate the commitmentId

  @Mandatory private BigInteger nonce;

  @Mandatory private BigInteger ttl;

  @Override
  public NamePreclaimTx toApiModel() {
    NamePreclaimTx namePreclaimTx = new NamePreclaimTx();
    namePreclaimTx.setAccountId(this.accountId);
    namePreclaimTx.setCommitmentId(EncodingUtils.generateCommitmentHash(this.name, this.salt));
    namePreclaimTx.setFee(this.fee);
    namePreclaimTx.setNonce(this.nonce);
    namePreclaimTx.setTtl(this.ttl);
    return namePreclaimTx;
  }

  @Override
  public Function<GenericTx, NamePreclaimTransactionModel> getApiToModelFunction() {
    return (tx) -> {
      NamePreclaimTx castedTx = (NamePreclaimTx) tx;
      return this.toBuilder()
          .accountId(castedTx.getAccountId())
          .fee(castedTx.getFee())
          .nonce(castedTx.getNonce())
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
    return NamePreclaimTransaction.builder().externalApi(externalApi).model(this).build();
  }
}
