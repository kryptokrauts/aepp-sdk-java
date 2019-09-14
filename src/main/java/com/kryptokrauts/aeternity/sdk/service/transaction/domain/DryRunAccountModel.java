package com.kryptokrauts.aeternity.sdk.service.transaction.domain;

import com.kryptokrauts.aeternity.generated.model.DryRunAccount;
import com.kryptokrauts.aeternity.sdk.domain.GenericInputObject;
import java.math.BigInteger;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class DryRunAccountModel extends GenericInputObject<DryRunAccount> {

  private String publicKey;

  private BigInteger amount;

  @Override
  public DryRunAccount mapToModel() {
    return new DryRunAccount().pubKey(publicKey).amount(amount);
  }

  @Override
  protected void validate() {
    if (this.amount == null) {
      this.amount = BigInteger.ZERO;
    }
  }
}
