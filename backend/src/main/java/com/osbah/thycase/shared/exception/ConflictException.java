package com.osbah.thycase.shared.exception;

import lombok.Getter;

@Getter
public class ConflictException extends RuntimeException {

    private final long errorCode;
    private final String errorMessage;

    public ConflictException(long errorCode, String message, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
        this.errorMessage = message;
    }

    public ConflictException(long errorCode, String message) {
        this.errorCode = errorCode;
        this.errorMessage = message;
    }
}
