package com.kryptokrauts.aeternity.sdk.service.oracle.domain;

import com.kryptokrauts.aeternity.generated.model.TTL.TypeEnum;
import com.kryptokrauts.aeternity.sdk.exception.NoSuchOracleTTLTypeException;
import lombok.Getter;

@Getter
public enum OracleTTLType {
  DELTA("delta"),

  BLOCK("block");

  private String value;

  OracleTTLType(String value) {
    this.value = value;
  }

  public TypeEnum toGeneratedEnum() {
    TypeEnum result = TypeEnum.fromValue(this.value);
    if (result == null) {
      throw new NoSuchOracleTTLTypeException(
          String.format("Given type %s cannot be mapped to valid oracle ttl type", this.value));
    }
    return result;
  }
}
