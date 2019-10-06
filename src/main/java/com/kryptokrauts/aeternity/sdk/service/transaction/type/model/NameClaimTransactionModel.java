package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.GenericTx;
import com.kryptokrauts.aeternity.generated.model.NameClaimTx;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.NameClaimTransaction;
import com.kryptokrauts.aeternity.sdk.util.ValidationUtil;
import com.kryptokrauts.sophia.compiler.generated.api.rxjava.DefaultApi;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@SuperBuilder(toBuilder = true)
public class NameClaimTransactionModel extends AbstractTransactionModel<NameClaimTx> {

  private static final BigInteger FEE_MULTIPLIER = new BigInteger("100000000000000");

  private static final BigInteger SMALLEST_FEE = new BigInteger("3").multiply(FEE_MULTIPLIER);

  private static Map<Integer, BigInteger> initialNameLengthFeeMap;

  {
    initialNameLengthFeeMap =
        new HashMap<Integer, BigInteger>() {
          {
            put(30, new BigInteger("5").multiply(FEE_MULTIPLIER));
            put(29, new BigInteger("8").multiply(FEE_MULTIPLIER));
            put(28, new BigInteger("13").multiply(FEE_MULTIPLIER));
            put(27, new BigInteger("21").multiply(FEE_MULTIPLIER));
            put(26, new BigInteger("34").multiply(FEE_MULTIPLIER));
            put(25, new BigInteger("55").multiply(FEE_MULTIPLIER));
            put(24, new BigInteger("89").multiply(FEE_MULTIPLIER));
            put(23, new BigInteger("144").multiply(FEE_MULTIPLIER));
            put(22, new BigInteger("233").multiply(FEE_MULTIPLIER));
            put(21, new BigInteger("377").multiply(FEE_MULTIPLIER));
            put(20, new BigInteger("610").multiply(FEE_MULTIPLIER));
            put(19, new BigInteger("987").multiply(FEE_MULTIPLIER));
            put(18, new BigInteger("1597").multiply(FEE_MULTIPLIER));
            put(17, new BigInteger("2584").multiply(FEE_MULTIPLIER));
            put(16, new BigInteger("4181").multiply(FEE_MULTIPLIER));
            put(15, new BigInteger("6765").multiply(FEE_MULTIPLIER));
            put(14, new BigInteger("10946").multiply(FEE_MULTIPLIER));
            put(13, new BigInteger("17711").multiply(FEE_MULTIPLIER));
            put(12, new BigInteger("28657").multiply(FEE_MULTIPLIER));
            put(11, new BigInteger("46368").multiply(FEE_MULTIPLIER));
            put(10, new BigInteger("75025").multiply(FEE_MULTIPLIER));
            put(9, new BigInteger("121393").multiply(FEE_MULTIPLIER));
            put(8, new BigInteger("196418").multiply(FEE_MULTIPLIER));
            put(7, new BigInteger("317811").multiply(FEE_MULTIPLIER));
            put(6, new BigInteger("514229").multiply(FEE_MULTIPLIER));
            put(5, new BigInteger("832040").multiply(FEE_MULTIPLIER));
            put(4, new BigInteger("1346269").multiply(FEE_MULTIPLIER));
            put(3, new BigInteger("2178309").multiply(FEE_MULTIPLIER));
            put(2, new BigInteger("3524578").multiply(FEE_MULTIPLIER));
            put(1, new BigInteger("5702887").multiply(FEE_MULTIPLIER));
          }
        };
  }

  private String accountId;
  private BigInteger nonce;
  private String name;
  private BigInteger nameSalt;
  private BigInteger nameFee;
  private BigInteger ttl;

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
    BigInteger minimumNameFee = initialNameLengthFeeMap.get(this.getNameLength());
    ValidationUtil.checkParameters(
        validate -> Optional.ofNullable(this.getNameFee().compareTo(minimumNameFee) != -1),
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
      int nameLength = this.getNameLength();
      if (nameLength >= 31) {
        this.nameFee = SMALLEST_FEE;
      }
      this.nameFee = initialNameLengthFeeMap.get(nameLength);
      log.info(
          String.format("initial name fee for the domain '%s' is: %s", this.name, this.nameFee));
    }
    return this.nameFee;
  }

  private Integer getNameLength() {
    return this.name.split("\\.")[0].length();
  }
}
