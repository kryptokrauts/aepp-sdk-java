package com.kryptokrauts.aeternity.sdk.exception;

public class AException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public AException() {}

  public AException(String message) {
    super(message);
  }

  public AException(Throwable e) {
    super(e);
  }

  public AException(String message, Throwable e) {
    super(message, e);
  }
}
