package com.okanetransfer.exception;

public class AgencyAlreadyExistsException extends RuntimeException {
    public AgencyAlreadyExistsException(String message) {
        super(message);
    }

    public AgencyAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
