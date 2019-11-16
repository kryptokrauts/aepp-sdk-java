package com.kryptokrauts.aeternity.sdk.domain;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@ToString
public class StringResultWrapper extends GenericResultObject<String, StringResultWrapper> {

  private String simpleValue;

  @Override
  protected StringResultWrapper map(String generatedResultObject) {
    if (generatedResultObject != null)
      return this.toBuilder().simpleValue(generatedResultObject).build();
    else return this.toBuilder().build();
  }

  @Override
  protected String getResultObjectClassName() {
    return this.getClass().getName();
  }
}
