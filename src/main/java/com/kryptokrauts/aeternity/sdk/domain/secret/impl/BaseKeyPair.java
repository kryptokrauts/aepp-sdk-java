package com.kryptokrauts.aeternity.sdk.domain.secret.impl;

import com.kryptokrauts.aeternity.sdk.constants.ApiIdentifiers;
import com.kryptokrauts.aeternity.sdk.domain.secret.KeyPair;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BaseKeyPair implements KeyPair<String> {

  private String publicKey;

  private String privateKey;

  @Builder
  public BaseKeyPair(final String publicKey, final String privateKey) {
    this.publicKey = publicKey;
    this.privateKey = privateKey;
  }

  public String getContractPK() {
    return ApiIdentifiers.CONTRACT_PUBKEY + publicKey.substring(2);
  }

  public String getOraclePK() {
    return ApiIdentifiers.ORACLE_PUBKEY + publicKey.substring(2);
  }
}
