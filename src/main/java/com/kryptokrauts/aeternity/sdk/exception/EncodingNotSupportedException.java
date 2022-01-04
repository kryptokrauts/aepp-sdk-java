package com.kryptokrauts.aeternity.sdk.exception;

public class EncodingNotSupportedException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public EncodingNotSupportedException() {}

  public EncodingNotSupportedException(String message) {
    super(message);
  }
}
