package com.osbah.thycase.shared.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {

    private final long errorCode;
    private final String errorMessage;

    public NotFoundException(long errorCode, String message) {
        this.errorCode = errorCode;
        this.errorMessage = message;
    }
}