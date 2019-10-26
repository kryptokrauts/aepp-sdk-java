package com.kryptokrauts.aeternity.sdk.service.oracle.domain;

import com.kryptokrauts.aeternity.generated.model.OracleQuery;
import com.kryptokrauts.aeternity.sdk.domain.GenericResultObject;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import java.math.BigInteger;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@ToString
public class OracleQueryResult extends GenericResultObject<OracleQuery, OracleQueryResult> {

  private String id;

  private String senderId;

  private BigInteger senderNonce;

  private String oracleId;

  private String query;

  private String response;

  private BigInteger ttl;

  private OracleTTLType responseTtl;

  private BigInteger fee;

  @Override
  protected OracleQueryResult map(OracleQuery generatedResultObject) {
    if (generatedResultObject != null)
      return this.toBuilder()
          .id(generatedResultObject.getId())
          .senderId(generatedResultObject.getSenderId())
          .senderNonce(generatedResultObject.getSenderNonce())
          .oracleId(generatedResultObject.getOracleId())
          .query(
              new String(EncodingUtils.decodeCheckWithIdentifier(generatedResultObject.getQuery())))
          .response(
              new String(
                  EncodingUtils.decodeCheckWithIdentifier(generatedResultObject.getResponse())))
          .ttl(generatedResultObject.getTtl())
          .responseTtl(OracleTTLType.fromTypeEnum(generatedResultObject.getResponseTtl()))
          .fee(generatedResultObject.getFee())
          .build();
    else return this.toBuilder().build();
  }

  @Override
  protected String getResultObjectClassName() {
    return this.getClass().getName();
  }
}
