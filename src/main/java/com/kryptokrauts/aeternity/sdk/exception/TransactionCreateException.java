package com.kryptokrauts.aeternity.sdk.exception;

public class TransactionCreateException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public TransactionCreateException(String message, Throwable e) {
    super(message, e);
  }
}
