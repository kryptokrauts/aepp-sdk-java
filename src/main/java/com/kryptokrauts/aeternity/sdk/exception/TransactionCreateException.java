package com.kryptokrauts.aeternity.sdk.exception;

public class TransactionCreateException extends RuntimeException {

	public TransactionCreateException(String message, Throwable e) {
		super(message, e);
	}

}
