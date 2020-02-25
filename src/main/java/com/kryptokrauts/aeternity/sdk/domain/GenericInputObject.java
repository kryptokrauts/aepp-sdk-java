package com.kryptokrauts.aeternity.sdk.domain;

import lombok.ToString;

/**
 * This class encapsules the generated input objects to keep the SDK stable in terms of changes
 * within the underlying AE protocol
 *
 * @param <T> the generated API model class
 */
@ToString
public abstract class GenericInputObject<T> {

  /**
   * validates and maps the wrapped model to generated API model and validates the input before it
   * is passed to the function call
   *
   * @return the mapped generated API model class
   */
  public T toGeneratedModel() {
    validate();
    return mapToModel();
  }

  /**
   * map to API model method implemented by the specific input model class
   *
   * @return the API input model
   */
  protected abstract T mapToModel();

  /** validate input if necessary */
  protected abstract void validate();
}
