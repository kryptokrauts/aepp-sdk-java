package com.kryptokrauts.aeternity.sdk.domain;

public abstract class GenericInputObject<T> {

  /**
   * maps the wrapped model to generated model and validates before
   *
   * @return
   */
  public T toGeneratedModel() {
    validate();
    return mapToModel();
  }

  /**
   * specific map to model method
   *
   * @return
   */
  protected abstract T mapToModel();

  /** validate input */
  protected abstract void validate();
}
