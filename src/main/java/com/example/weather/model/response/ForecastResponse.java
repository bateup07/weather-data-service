package com.example.weather.model.response;

import com.example.weather.model.ForecastDay;

import java.util.List;

/**
 * Daily forecast for one matching location.
 *
 * @param location  place name
 * @param country   country code
 * @param state     administrative area
 * @param latitude  location latitude
 * @param longitude location longitude
 * @param forecast  daily forecast entries
 */
public record ForecastResponse(
        String location,
        String country,
        String state,
        double latitude,
        double longitude,
        List<ForecastDay> forecast
) {
}
