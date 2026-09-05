package com.example.weather.exception;

/**
 * Raised when a caller supplies invalid input.
 */
public class InvalidRequestException extends RuntimeException {

    /**
     * Creates the exception.
     *
     * @param message description of the invalid input
     */
    public InvalidRequestException(final String message) {
        super(message);
    }
}
