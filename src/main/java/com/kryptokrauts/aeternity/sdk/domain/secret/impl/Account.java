package com.kryptokrauts.aeternity.sdk.domain.secret.impl;

import com.kryptokrauts.aeternity.sdk.constants.ApiIdentifiers;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Account {

  private String address;

  private String privateKey;

  @Builder
  public Account(final String address, final String privateKey) {
    this.address = address;
    this.privateKey = privateKey;
  }

  public String getContractPK() {
    return ApiIdentifiers.CONTRACT_PUBKEY + address.substring(2);
  }

  public String getOraclePK() {
    return ApiIdentifiers.ORACLE_PUBKEY + address.substring(2);
  }
}
