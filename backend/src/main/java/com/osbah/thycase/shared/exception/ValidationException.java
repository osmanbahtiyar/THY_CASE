package com.osbah.thycase.shared.exception;

import lombok.Getter;

@Getter
public class ValidationException extends RuntimeException {

    private final long errorCode;
    private final String errorMessage;

    public ValidationException(long errorCode, String message) {
        this.errorCode = errorCode;
        this.errorMessage = message;
    }
}