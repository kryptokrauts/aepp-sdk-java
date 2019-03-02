package com.kryptokrauts.aeternity.sdk.exception;

public class EncodingNotSupportedException extends RuntimeException {

  public EncodingNotSupportedException() {}

  public EncodingNotSupportedException(String message) {
    super(message);
  }
}
