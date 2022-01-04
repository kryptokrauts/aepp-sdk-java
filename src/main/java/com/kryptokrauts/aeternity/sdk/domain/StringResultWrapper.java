package com.kryptokrauts.aeternity.sdk.domain;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * This string result wrapper introduces the error handling when returning simple string from node
 * calls
 */
@Getter
@SuperBuilder(toBuilder = true)
@ToString
public class StringResultWrapper extends GenericResultObject<String, StringResultWrapper> {

  private String result;

  @Override
  protected StringResultWrapper map(String generatedResultObject) {
    if (generatedResultObject != null)
      return this.toBuilder().result(generatedResultObject).build();
    else return this.toBuilder().build();
  }

  @Override
  protected String getResultObjectClassName() {
    return this.getClass().getName();
  }
}
