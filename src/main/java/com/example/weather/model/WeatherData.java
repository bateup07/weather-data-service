package com.example.weather.model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Domain weather for a coordinate.
 *
 * @param temperature current temperature
 * @param condition   weather description
 * @param windSpeed   current wind speed
 * @param humidity    current relative humidity
 * @param timestamp   observation time
 * @param daily       daily forecast entries
 */
public record WeatherData(
        double temperature,
        String condition,
        double windSpeed,
        int humidity,
        LocalDateTime timestamp,
        List<DailyWeatherData> daily
) {
}
