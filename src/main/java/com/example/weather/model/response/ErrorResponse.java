package com.example.weather.model.response;

import java.time.Instant;

/**
 * Standard API error body.
 *
 * @param timestamp when the error occurred
 * @param status    HTTP status code
 * @param error     HTTP reason phrase
 * @param message   error detail
 */
public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message
) {
}