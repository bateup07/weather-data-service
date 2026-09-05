package com.example.weather.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Daily forecast series in an Open-Meteo forecast response.
 *
 * @param time                         forecast dates
 * @param weatherCode                  WMO weather codes
 * @param temperature2mMax             maximum temperatures
 * @param temperature2mMin             minimum temperatures
 * @param precipitationProbabilityMax  precipitation chances
 */
public record OpenMeteoDaily(
        List<String> time,
        @JsonProperty("weather_code")
        List<Double> weatherCode,
        @JsonProperty("temperature_2m_max")
        List<Double> temperature2mMax,
        @JsonProperty("temperature_2m_min")
        List<Double> temperature2mMin,
        @JsonProperty("precipitation_probability_max")
        List<Double> precipitationProbabilityMax) {
}
