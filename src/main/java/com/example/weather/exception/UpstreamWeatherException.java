package com.example.weather.exception;

/**
 * Raised when the upstream weather provider cannot be used.
 */
public class UpstreamWeatherException extends RuntimeException {

    /**
     * Creates the exception.
     *
     * @param message description of the provider failure
     */
    public UpstreamWeatherException(String message) {
        super(message);
    }

    /**
     * Creates the exception with an underlying cause.
     *
     * @param message description of the provider failure
     * @param cause   original failure
     */
    public UpstreamWeatherException(
            String message,
            Throwable cause) {

        super(message, cause);
    }
}
