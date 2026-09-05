package com.example.weather.model.response;

import com.example.weather.model.OpenMeteoLocation;

import java.util.List;

/**
 * Raw Open-Meteo geocoding payload.
 *
 * @param results matching locations from the provider
 */
public record OpenMeteoGeocodingResponse(
        List<OpenMeteoLocation> results) {
}
