package com.kryptokrauts.aeternity.sdk.exception;

public class TransactionWaitTimeoutExpiredException extends AException {

  private static final long serialVersionUID = 1L;

  public TransactionWaitTimeoutExpiredException(String message) {
    super(message);
  }
}
