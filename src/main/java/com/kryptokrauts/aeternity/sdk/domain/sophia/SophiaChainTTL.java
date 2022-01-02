package com.kryptokrauts.aeternity.sdk.domain.sophia;

import java.math.BigInteger;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
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

  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    SophiaChainTTL other = (SophiaChainTTL) obj;
    if (ttl == null) {
      if (other.ttl == null) {
        return false;
      }
    } else if (!ttl.equals(other.ttl)) {
      return false;
    }
    ;
    return true;
  }

  public enum Type {
    RelativeTTL,
    FixedTTL
  }
}
