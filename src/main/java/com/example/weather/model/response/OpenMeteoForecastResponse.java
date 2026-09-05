package com.example.weather.model.response;

import com.example.weather.model.OpenMeteoCurrent;
import com.example.weather.model.OpenMeteoDaily;

/**
 * Raw Open-Meteo forecast payload.
 *
 * @param current current conditions
 * @param daily   daily forecast series
 */
public record OpenMeteoForecastResponse(
        OpenMeteoCurrent current,
        OpenMeteoDaily daily) {
}
