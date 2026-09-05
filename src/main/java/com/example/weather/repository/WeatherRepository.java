package com.example.weather.repository;

import com.example.weather.model.LocationData;
import com.example.weather.model.WeatherData;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Reads locations and weather from an upstream provider.
 */
public interface WeatherRepository {

    /**
     * Finds locations whose names match the given value.
     *
     * @param location city or place name to search
     * @return matching locations
     */
    Mono<List<LocationData>> findLocations(String location);

    /**
     * Fetches current weather and daily forecast for a coordinate.
     *
     * @param latitude  location latitude
     * @param longitude location longitude
     * @return weather for that point
     */
    Mono<WeatherData> getWeather(double latitude, double longitude);
}
