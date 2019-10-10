package com.kryptokrauts.aeternity.sdk.service.transaction.domain;

import com.kryptokrauts.aeternity.generated.model.DryRunCallReq;
import com.kryptokrauts.aeternity.sdk.domain.GenericInputObject;
import java.math.BigInteger;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class DryRunCallRequestModel extends GenericInputObject<DryRunCallReq> {

  private String calldata;

  private String contract;

  private BigInteger amount;

  private BigInteger gas;

  private String caller;

  private BigInteger nonce;

  private BigInteger abiVersion;

  @Default private DryRunCallContextModel context = null;

  @Override
  protected DryRunCallReq mapToModel() {
    return new DryRunCallReq()
        .calldata(calldata)
        .contract(contract)
        .amount(amount)
        .gas(gas)
        .caller(caller)
        .nonce(nonce)
        .abiVersion(abiVersion)
        .context(context != null ? context.toGeneratedModel() : null);
  }

  @Override
  protected void validate() {
    // TODO Auto-generated method stub

  }
}
