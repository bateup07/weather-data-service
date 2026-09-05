package com.example.weather.repository;

import org.springframework.stereotype.Component;

/**
 * Converts WMO weather codes into short English descriptions.
 */
@Component
public class WeatherCodeMapper {

    /**
     * Returns a description for the given WMO weather code.
     *
     * @param weatherCode WMO weather interpretation code
     * @return human-readable condition, or {@code Unknown} when the code is not recognised
     */
    public String toDescription(final int weatherCode) {
        return switch (weatherCode) {
            case 0 ->
                    "Clear sky";
            case 1, 2, 3 ->
                    "Mainly clear, partly cloudy or overcast";
            case 45, 48 ->
                    "Fog";
            case 51, 53, 55 ->
                    "Drizzle";
            case 56, 57 ->
                    "Freezing drizzle";
            case 61, 63, 65 ->
                    "Rain";
            case 66, 67 ->
                    "Freezing rain";
            case 71, 73, 75, 77 ->
                    "Snow";
            case 80, 81, 82 ->
                    "Rain showers";
            case 85, 86 ->
                    "Snow showers";
            case 95 ->
                    "Thunderstorm";
            case 96, 99 ->
                    "Thunderstorm with hail";
            default ->
                    "Unknown";
        };
    }
}