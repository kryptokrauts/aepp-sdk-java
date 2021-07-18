package com.kryptokrauts.aeternity.sdk.service.account.domain;

import com.kryptokrauts.aeternity.generated.model.Account;
import com.kryptokrauts.aeternity.sdk.domain.GenericResultObject;
import java.math.BigInteger;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@ToString
public class AccountResult extends GenericResultObject<Account, AccountResult> {

  private String publicKey;

  private BigInteger balance;

  private BigInteger nonce;

  private boolean payable;

  private String kind;

  private String gaContractId;

  private String gaAuthenticationFunction;

  @Override
  protected AccountResult map(Account generatedResultObject) {
    if (generatedResultObject != null)
      return this.toBuilder()
          .publicKey(generatedResultObject.getId())
          .balance(generatedResultObject.getBalance())
          .nonce(generatedResultObject.getNonce())
          .payable(generatedResultObject.getPayable())
          .kind(generatedResultObject.getKind().toString())
          .gaContractId(generatedResultObject.getContractId())
          .gaAuthenticationFunction(generatedResultObject.getAuthFun())
          .build();
    else return this.toBuilder().build();
  }

  @Override
  protected String getResultObjectClassName() {
    return AccountResult.class.getName();
  }
}
