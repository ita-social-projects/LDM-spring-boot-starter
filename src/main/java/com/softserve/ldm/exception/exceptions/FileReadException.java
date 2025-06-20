package com.softserve.ldm.exception.exceptions;

import lombok.experimental.StandardException;

/**
 * Exception that is thrown when the file could not be read
 */
@StandardException
public class FileReadException extends RuntimeException {
    public FileReadException(String message) {
        super(message);
    }
}
