package com.ivan.softserve.ldm.exception.exceptions;

import lombok.experimental.StandardException;


/**
 * Exception that is thrown when something (e.g. file) is not found.
 */
@StandardException
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}