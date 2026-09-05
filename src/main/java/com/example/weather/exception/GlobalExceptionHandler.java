package com.example.weather.exception;

import com.example.weather.model.response.ErrorResponse;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;

/**
 * Maps application exceptions to HTTP error responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Returns HTTP 404 when a location cannot be found.
     *
     * @param exception missing-location error
     * @return not-found response
     */
    @ExceptionHandler(LocationNotFoundException.class)
    public ResponseEntity<ErrorResponse> locationNotFound(
            final LocationNotFoundException exception) {
        return response(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    /**
     * Returns HTTP 503 when the weather provider fails.
     *
     * @param exception upstream provider error
     * @return service-unavailable response
     */
    @ExceptionHandler(UpstreamWeatherException.class)
    public ResponseEntity<ErrorResponse> upstreamFailure(
            final UpstreamWeatherException exception) {
        return response(HttpStatus.SERVICE_UNAVAILABLE,
                "Weather provider is currently unavailable");
    }

    /**
     * Returns HTTP 503 when the circuit breaker is open.
     *
     * @param exception circuit-open error
     * @return service-unavailable response
     */
    @ExceptionHandler(CallNotPermittedException.class)
    public ResponseEntity<ErrorResponse> circuitOpen(
            final CallNotPermittedException exception) {
        return response(HttpStatus.SERVICE_UNAVAILABLE,
                "Weather provider is temporarily unavailable");
    }

    /**
     * Returns HTTP 429 when the provider rate limit is exhausted.
     *
     * @param exception rate-limit error
     * @return too-many-requests response
     */
    @ExceptionHandler(RequestNotPermitted.class)
    public ResponseEntity<ErrorResponse> rateLimited(
            final RequestNotPermitted exception) {
        return response(HttpStatus.TOO_MANY_REQUESTS,
                "Weather provider request limit has been reached");
    }

    /**
     * Returns HTTP 400 for invalid request input.
     *
     * @param exception validation or type-mismatch error
     * @return bad-request response
     */
    @ExceptionHandler({
            InvalidRequestException.class,
            ConstraintViolationException.class,
            MethodArgumentNotValidException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<ErrorResponse> badRequest(final Exception exception) {
        return response(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    private ResponseEntity<ErrorResponse> response(
            final HttpStatus status,
            final String message) {
        return ResponseEntity.status(status).body(
                new ErrorResponse(
                        Instant.now(),
                        status.value(),
                        status.getReasonPhrase(),
                        message));
    }
}
