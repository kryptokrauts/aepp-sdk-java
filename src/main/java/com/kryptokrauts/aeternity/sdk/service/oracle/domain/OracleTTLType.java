package com.kryptokrauts.aeternity.sdk.service.oracle.domain;

import com.kryptokrauts.aeternity.generated.model.TTL;
import com.kryptokrauts.aeternity.generated.model.TTL.TypeEnum;
import com.kryptokrauts.aeternity.sdk.exception.NoSuchOracleTTLTypeException;
import java.math.BigInteger;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum OracleTTLType {
  DELTA("delta"),

  BLOCK("block");

  private String ttlTypeName;

  private BigInteger value;

  OracleTTLType(String ttlTypeName) {
    this.ttlTypeName = ttlTypeName;
  }

  public TypeEnum toGeneratedEnum() {
    TypeEnum result = TypeEnum.fromValue(this.ttlTypeName);
    if (result == null) {
      throw new NoSuchOracleTTLTypeException(
          String.format(
              "Given type %s cannot be mapped to valid oracle ttl type", this.ttlTypeName));
    }
    return result;
  }

  public static OracleTTLType fromTypeEnum(TTL ttl) {
    OracleTTLType mappedType = null;
    switch (ttl.getType()) {
      case BLOCK:
        mappedType = BLOCK;
        break;
      case DELTA:
        mappedType = DELTA;
        break;
      default:
        throw new NoSuchOracleTTLTypeException(
            String.format("Type %s cannot be mapped, check AE version", ttl.getType()));
    }
    mappedType.value = ttl.getValue();
    return mappedType;
  }
}
