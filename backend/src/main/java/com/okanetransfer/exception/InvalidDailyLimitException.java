package com.okanetransfer.exception;

public class InvalidDailyLimitException extends RuntimeException {
    public InvalidDailyLimitException(String message) {
        super(message);
    }

    public InvalidDailyLimitException(String message, Throwable cause) {
        super(message, cause);
    }
}
