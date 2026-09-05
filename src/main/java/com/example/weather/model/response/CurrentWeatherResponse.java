package com.example.weather.model.response;

import java.time.LocalDateTime;

/**
 * Current weather for one matching location.
 *
 * @param location    place name
 * @param country     country code
 * @param state       administrative area
 * @param latitude    location latitude
 * @param longitude   location longitude
 * @param temperature current temperature
 * @param condition   weather description
 * @param windSpeed   current wind speed
 * @param humidity    current relative humidity
 * @param timestamp   observation time
 */
public record CurrentWeatherResponse(
        String location,
        String country,
        String state,
        double latitude,
        double longitude,
        double temperature,
        String condition,
        double windSpeed,
        int humidity,
        LocalDateTime timestamp
) {
}