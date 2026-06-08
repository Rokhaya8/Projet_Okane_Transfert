package com.okanetransfer.exception;

public class CashDrawerAlreadyClosedException extends RuntimeException {
    public CashDrawerAlreadyClosedException(String message) {
        super(message);
    }
}
