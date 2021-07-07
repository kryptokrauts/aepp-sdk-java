package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.GenericTx;
import com.kryptokrauts.aeternity.generated.model.NameClaimTx;
import com.kryptokrauts.aeternity.sdk.annotations.Mandatory;
import com.kryptokrauts.aeternity.sdk.constants.AENS;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.NameClaimTransaction;
import com.kryptokrauts.aeternity.sdk.util.ValidationUtil;
import com.kryptokrauts.sophia.compiler.generated.api.rxjava.DefaultApi;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.function.Function;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@SuperBuilder(toBuilder = true)
@ToString
public class NameClaimTransactionModel extends AbstractTransactionModel<NameClaimTx> {

  @Mandatory private String accountId;
  @Mandatory private BigInteger nonce;
  @Mandatory private String name;
  @Mandatory private BigInteger nameSalt;
  @Default private BigInteger ttl = BigInteger.ZERO;

  private BigInteger nameFee;

  @Override
  public NameClaimTx toApiModel() {
    NameClaimTx nameClaimTx = new NameClaimTx();
    nameClaimTx.setAccountId(this.accountId);
    nameClaimTx.setNonce(this.nonce);
    nameClaimTx.setName(this.name);
    nameClaimTx.setNameSalt(this.nameSalt);
    nameClaimTx.setNameFee(this.getNameFee());
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
          .nameFee(castedTx.getNameFee())
          .ttl(castedTx.getTtl())
          .build();
    };
  }

  @Override
  public void validateInput() {
    ValidationUtil.checkNamespace(this.name);
    BigInteger minimumNameFee = AENS.getInitialNameFee(this.name);
    ValidationUtil.checkParameters(
        validate -> this.getNameFee().compareTo(minimumNameFee) != -1,
        this.nameFee,
        "validateNameClaimTransaction",
        Arrays.asList("nameFee", this.getNameFee().toString()),
        String.format(ValidationUtil.NAME_FEE_TOO_LOW, minimumNameFee));
  }

  @Override
  public AbstractTransaction<?> buildTransaction(ExternalApi externalApi, DefaultApi compilerApi) {
    return NameClaimTransaction.builder().externalApi(externalApi).model(this).build();
  }

  /**
   * special getter which uses the initial required nameFee if a name is present and no nameFee is
   * set
   *
   * @return the nameFee
   */
  public BigInteger getNameFee() {
    if (this.nameFee == null && this.name != null) {
      log.info(
          "nameFee not provided. using the initial required fee for the length of the given name.");
      this.nameFee = AENS.getInitialNameFee(name);
      log.info("initial name fee for the domain '{}' is: {}", this.name, this.nameFee);
    }
    return this.nameFee;
  }
}
