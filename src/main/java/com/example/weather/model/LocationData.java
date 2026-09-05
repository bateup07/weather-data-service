package com.example.weather.model;

/**
 * Domain location used by services and repositories.
 *
 * @param name      place name
 * @param country   country code
 * @param state     administrative area
 * @param latitude  location latitude
 * @param longitude location longitude
 */
public record LocationData(
        String name,
        String country,
        String state,
        double latitude,
        double longitude
) {
}
