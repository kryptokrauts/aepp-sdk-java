package com.kryptokrauts.aeternity.sdk.domain.sophia;

import java.math.BigInteger;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
public class SophiaChainTTL extends SophiaType {

  private BigInteger ttl;
  private Type ttlType;

  public SophiaChainTTL(BigInteger ttl, Type ttlType) {
    this.ttl = ttl;
    this.ttlType = ttlType;
  }

  public BigInteger getTtl() {
    return this.ttl;
  }

  public Type getTTLType() {
    return this.ttlType;
  }

  @Override
  public String getCompilerValue() {
    return ttlType.toString() + "(" + ttl + ")";
  }

  public enum Type {
    RelativeTTL,
    FixedTTL
  }
}
