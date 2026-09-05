package com.example.weather.service.impl;

import com.example.weather.exception.InvalidRequestException;
import com.example.weather.model.response.CurrentWeatherResponse;
import com.example.weather.model.DailyWeatherData;
import com.example.weather.model.ForecastDay;
import com.example.weather.model.response.ForecastResponse;
import com.example.weather.model.LocationData;
import com.example.weather.repository.WeatherRepository;
import com.example.weather.service.WeatherService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Resolves a place name to matching locations and fetches weather for each one.
 */
@Service
public class WeatherServiceImpl implements WeatherService {

    private static final int MAX_CONCURRENT_WEATHER_REQUESTS = 5;

    private final WeatherRepository weatherRepository;

    /**
     * Creates the service.
     *
     * @param weatherRepository source of locations and weather data
     */
    public WeatherServiceImpl(final WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "currentWeather", key = "#location.trim().toLowerCase()")
    public Mono<List<CurrentWeatherResponse>> getCurrentWeather(final String location) {
        return weatherRepository.findLocations(location.trim())
                .flatMapMany(Flux::fromIterable)
                .flatMap(this::toCurrentWeather, MAX_CONCURRENT_WEATHER_REQUESTS)
                .collectList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "forecast", key = "#location.trim().toLowerCase() + '-' + #days")
    public Mono<List<ForecastResponse>> getForecast(final String location, final int days) {
        if (days < 1 || days > 5) {
            return Mono.error(new InvalidRequestException(
                    "Forecast days must be between 1 and 5"));
        }

        return weatherRepository.findLocations(location.trim())
                .flatMapMany(Flux::fromIterable)
                .flatMap(loc -> toForecast(loc, days), MAX_CONCURRENT_WEATHER_REQUESTS)
                .collectList();
    }

    private Mono<CurrentWeatherResponse> toCurrentWeather(final LocationData location) {
        return weatherRepository.getWeather(location.latitude(), location.longitude())
                .map(weather -> new CurrentWeatherResponse(
                        location.name(),
                        location.country(),
                        location.state(),
                        location.latitude(),
                        location.longitude(),
                        weather.temperature(),
                        weather.condition(),
                        weather.windSpeed(),
                        weather.humidity(),
                        weather.timestamp()
                ));
    }

    private Mono<ForecastResponse> toForecast(final LocationData location, final int days) {
        return weatherRepository.getWeather(location.latitude(), location.longitude())
                .map(weather -> new ForecastResponse(
                        location.name(),
                        location.country(),
                        location.state(),
                        location.latitude(),
                        location.longitude(),
                        weather.daily().stream()
                                .limit(days)
                                .map(this::toForecastDay)
                                .toList()
                ));
    }

    private ForecastDay toForecastDay(final DailyWeatherData weather) {
        return new ForecastDay(
                weather.date(),
                weather.highTemperature(),
                weather.lowTemperature(),
                weather.condition(),
                weather.precipitationChance()
        );
    }
}
