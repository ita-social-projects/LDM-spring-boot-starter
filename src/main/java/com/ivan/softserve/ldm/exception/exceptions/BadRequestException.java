package com.ivan.softserve.ldm.exception.exceptions;

import lombok.experimental.StandardException;

/**
 * Exception that is thrown when user trying to pass bad request.
 */
@StandardException
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
