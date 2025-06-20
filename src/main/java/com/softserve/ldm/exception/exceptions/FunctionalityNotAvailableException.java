package com.softserve.ldm.exception.exceptions;

import lombok.experimental.StandardException;

/**
 * Exception that is thrown when endpoints is unavailable when secretKeys.env file is deleted .
 */
@StandardException
public class FunctionalityNotAvailableException extends RuntimeException {
    public FunctionalityNotAvailableException(String message) {
        super(message);
    }
}
