package com.kryptokrauts.aeternity.sdk.domain.secret;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/** Data structure to hold one derived key from the tree of hierarchic keys */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class HdKeyPair extends KeyPair {

  /** if using hd wallet this chaincode is used to derive subsequent key pairs */
  @ToString.Exclude private byte[] chainCode;

  /** index of the keyPair within the hierarchic tree */
  private int index;

  public HdKeyPair(
      final byte[] rawPublicKey, final byte[] rawPrivateKey, byte[] chainCode, int index) {
    super(rawPublicKey, rawPrivateKey);
    this.chainCode = chainCode;
    this.index = index;
  }

  public static HdKeyPair fromKeyPair(KeyPair keyPair, byte[] chainCode, int index) {
    return new HdKeyPair(keyPair.getRawPublicKey(), keyPair.getRawPrivateKey(), chainCode, index);
  }
}
