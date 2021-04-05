package com.kryptokrauts.aeternity.sdk.domain.secret;

import com.kryptokrauts.aeternity.sdk.constants.ApiIdentifiers;
import com.kryptokrauts.aeternity.sdk.util.ByteUtils;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.bouncycastle.util.encoders.Hex;

@Data
@NoArgsConstructor
public class KeyPair {

  @ToString.Exclude private byte[] rawPublicKey;

  @ToString.Exclude private byte[] rawPrivateKey;

  /** hex encoded private key */
  private String encodedPrivateKey;

  /** base58 encoded human readable publicKey */
  private String address;

  @Builder
  public KeyPair(final byte[] rawPublicKey, final byte[] rawPrivateKey) {
    this.rawPublicKey = rawPublicKey;
    this.rawPrivateKey = rawPrivateKey;
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
