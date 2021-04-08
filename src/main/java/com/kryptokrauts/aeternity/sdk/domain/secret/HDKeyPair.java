package com.kryptokrauts.aeternity.sdk.domain.secret;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class HDKeyPair extends KeyPair {

  /** if using hd wallet this chaincode is used to derive subsequent key pairs */
  @ToString.Exclude private byte[] chainCode;

  /** index of the keyPair within the hierarchic tree */
  private int index;

  public HDKeyPair(
      final byte[] rawPublicKey, final byte[] rawPrivateKey, byte[] chainCode, int index) {
    super(rawPublicKey, rawPrivateKey);
    this.chainCode = chainCode;
    this.index = index;
  }

  public static HDKeyPair fromKeyPair(KeyPair keyPair, byte[] chainCode, int index) {
    return new HDKeyPair(keyPair.getRawPublicKey(), keyPair.getRawPrivateKey(), chainCode, index);
  }
}
