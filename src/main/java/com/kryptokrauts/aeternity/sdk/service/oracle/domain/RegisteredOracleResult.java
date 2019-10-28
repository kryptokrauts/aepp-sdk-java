package com.kryptokrauts.aeternity.sdk.service.oracle.domain;

import com.kryptokrauts.aeternity.generated.model.RegisteredOracle;
import com.kryptokrauts.aeternity.sdk.domain.GenericResultObject;
import java.math.BigInteger;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@ToString
public class RegisteredOracleResult
    extends GenericResultObject<RegisteredOracle, RegisteredOracleResult> {
  private String id;

  private String queryFormat;

  private String responseFormat;

  private BigInteger queryFee;

  private BigInteger ttl;

  private BigInteger abiVersion;

  @Override
  protected RegisteredOracleResult map(RegisteredOracle generatedResultObject) {
    if (generatedResultObject != null)
      return this.toBuilder()
          .id(generatedResultObject.getId())
          .queryFormat(generatedResultObject.getQueryFormat())
          .responseFormat(generatedResultObject.getResponseFormat())
          .queryFee(generatedResultObject.getQueryFee())
          .ttl(generatedResultObject.getTtl())
          .abiVersion(generatedResultObject.getAbiVersion())
          .build();
    else return this.toBuilder().build();
  }

  @Override
  protected String getResultObjectClassName() {
    return this.getClass().getName();
  }
}
