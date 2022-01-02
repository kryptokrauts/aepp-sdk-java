package com.kryptokrauts.aeternity.sdk.domain.sophia;

public abstract class SophiaType {

  /**
   * @return the string-representation of the Sophia type that needs to be provided to the compiler
   */
  public abstract String getCompilerValue();
}
