package com.example.weather.model.response;

/**
 * Location returned by a geocoding search.
 *
 * @param name      place name
 * @param country   country code
 * @param state     administrative area
 * @param latitude  location latitude
 * @param longitude location longitude
 */
public record LocationResponse(
        String name,
        String country,
        String state,
        double latitude,
        double longitude
) {
}
