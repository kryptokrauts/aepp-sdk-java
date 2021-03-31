package com.kryptokrauts.aeternity.sdk.domain.secret;

import org.bouncycastle.util.encoders.Hex;
import com.kryptokrauts.aeternity.sdk.constants.ApiIdentifiers;
import com.kryptokrauts.aeternity.sdk.util.ByteUtils;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class KeyPair {
  private byte[] rawPublicKey;

  private byte[] rawPrivateKey;

  /**
   * if using hd wallet this chaincode is used to derive subsequent key pairs
   */
  private byte[] chainCode;

  /**
   * hex encoded private key
   */
  private String encodedPrivateKey;

  /**
   * base58 encoded human readable publicKey
   */
  private String address;

  @Builder
  public KeyPair(final byte[] rawPublicKey, final byte[] rawPrivateKey, final byte[] chainCode) {
    this.rawPublicKey = rawPublicKey;
    this.rawPrivateKey = rawPrivateKey;
    this.chainCode = chainCode;
    this.encodedPrivateKey = Hex.toHexString(rawPrivateKey) + Hex.toHexString(rawPublicKey);
    this.address = EncodingUtils.encodeCheck(rawPublicKey, ApiIdentifiers.ACCOUNT_PUBKEY);
  }

  public byte[] getConcatenatedPrivateKey() {
    return ByteUtils.concatenate(rawPrivateKey, rawPublicKey);
  }

  public String getContractPK() {
    return ApiIdentifiers.CONTRACT_PUBKEY + address.substring(2);
  }

  public String getOraclePK() {
    return ApiIdentifiers.ORACLE_PUBKEY + address.substring(2);
  }
}
