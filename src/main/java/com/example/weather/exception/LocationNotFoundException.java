package com.example.weather.exception;

/**
 * Raised when a place name does not match any known location.
 */
public class LocationNotFoundException extends RuntimeException {

    /**
     * Creates the exception for the requested place name.
     *
     * @param location place name that could not be found
     */
    public LocationNotFoundException(String location) {
        super("Location not found: " + location);
    }
}
