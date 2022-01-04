package com.kryptokrauts.aeternity.sdk.domain;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * This object result wrapper introduces the error handling when returning simple string from node
 * calls
 */
@Getter
@SuperBuilder(toBuilder = true)
@ToString
public class ObjectResultWrapper extends GenericResultObject<Object, ObjectResultWrapper> {

  private Object result;

  @Override
  protected ObjectResultWrapper map(Object generatedResultObject) {
    if (generatedResultObject != null)
      return this.toBuilder().result(generatedResultObject).build();
    else return this.toBuilder().build();
  }

  @Override
  protected String getResultObjectClassName() {
    return this.getClass().getName();
  }
}
