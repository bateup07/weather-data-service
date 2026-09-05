package com.example.weather.service;

import com.example.weather.model.response.CurrentWeatherResponse;
import com.example.weather.model.response.ForecastResponse;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Looks up current weather and forecasts for a place name.
 */
public interface WeatherService {

    /**
     * Returns current weather for every location matching the given name.
     *
     * @param location city or place name to look up
     * @return current weather for each matching location
     */
    Mono<List<CurrentWeatherResponse>> getCurrentWeather(String location);

    /**
     * Returns a daily forecast for every location matching the given name.
     *
     * @param location city or place name to look up
     * @param days     number of forecast days, from 1 to 5
     * @return forecast for each matching location
     */
    Mono<List<ForecastResponse>> getForecast(String location, int days);
}
