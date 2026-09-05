package com.example.weather.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * One location in an Open-Meteo geocoding response.
 *
 * @param name        place name
 * @param countryCode country code
 * @param admin1      administrative area
 * @param latitude    location latitude
 * @param longitude   location longitude
 */
public record OpenMeteoLocation(
        String name,
        @JsonProperty("country_code")
        String countryCode,
        String admin1,
        double latitude,
        double longitude) {
}
