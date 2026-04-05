package com.microbank.shared.exceptions;

public class OperationLimitExceededException extends RuntimeException {

    public OperationLimitExceededException(String message) {
        super(message);
    }

    public OperationLimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}
