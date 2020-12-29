// package com.kryptokrauts.aeternity.sdk.service.indaex.domain;
//
// import com.kryptokrauts.aeternal.generated.model.ActiveNameAuctionsCount;
// import com.kryptokrauts.aeternity.sdk.domain.GenericResultObject;
// import java.math.BigInteger;
// import lombok.Getter;
// import lombok.ToString;
// import lombok.experimental.SuperBuilder;
//
// @Getter
// @SuperBuilder(toBuilder = true)
// @ToString
// public class ActiveNameAuctionsCountResult
//    extends GenericResultObject<ActiveNameAuctionsCount, ActiveNameAuctionsCountResult> {
//
//  private BigInteger count;
//  private String result;
//
//  @Override
//  protected ActiveNameAuctionsCountResult map(ActiveNameAuctionsCount generatedResultObject) {
//    if (generatedResultObject != null)
//      return this.toBuilder()
//          .count(generatedResultObject.getCount())
//          .result(generatedResultObject.getResult())
//          .build();
//    else return this.toBuilder().build();
//  }
//
//  @Override
//  protected String getResultObjectClassName() {
//    return ActiveNameAuctionsCountResult.class.getName();
//  }
// }
