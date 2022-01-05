package com.kryptokrauts.aeternity.sdk.exception;

public class DebugModeNotEnabledException extends AException {

  private static final long serialVersionUID = 1L;

  public DebugModeNotEnabledException(String msg) {
    super(msg);
  }
}
