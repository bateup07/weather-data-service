package com.example.weather.repository;

import com.example.weather.exception.LocationNotFoundException;
import com.example.weather.exception.UpstreamWeatherException;
import com.example.weather.model.LocationData;
import com.example.weather.model.WeatherData;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Weather repository backed by Open-Meteo, with retry, rate limiting and a circuit breaker.
 */
@Repository
public class OpenMeteoRepository implements WeatherRepository {

    private final OpenMeteoClient client;
    private final OpenMeteoMapper mapper;

    /**
     * Creates the repository.
     *
     * @param client HTTP client for Open-Meteo
     * @param mapper converter from Open-Meteo payloads to domain models
     */
    public OpenMeteoRepository(final OpenMeteoClient client, final OpenMeteoMapper mapper) {
        this.client = client;
        this.mapper = mapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CircuitBreaker(name = "weather")
    @RateLimiter(name = "weather")
    @Retry(name = "weather")
    public Mono<List<LocationData>> findLocations(final String location) {
        return client.findLocations(location)
                .switchIfEmpty(Mono.error(
                        new LocationNotFoundException(location)))
                .map(response -> mapper.toLocationData(
                        response,
                        location))
                .onErrorMap(
                        LocationNotFoundException.class,
                        exception -> exception)
                .onErrorMap(
                        exception -> !(exception instanceof UpstreamWeatherException)
                                && !(exception instanceof LocationNotFoundException),
                        exception -> new UpstreamWeatherException(
                                "Unable to retrieve location from weather provider",
                                exception));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CircuitBreaker(name = "weather")
    @RateLimiter(name = "weather")
    @Retry(name = "weather")
    public Mono<WeatherData> getWeather(final double latitude, final double longitude) {
        return client.getWeather(latitude, longitude)
                .switchIfEmpty(Mono.error(
                        new UpstreamWeatherException(
                                "Weather provider returned an empty response")))
                .map(mapper::toWeatherData)
                .onErrorMap(
                        UpstreamWeatherException.class,
                        exception -> exception)
                .onErrorMap(
                        exception -> !(exception instanceof UpstreamWeatherException),
                        exception -> new UpstreamWeatherException(
                                "Unable to retrieve weather data",
                                exception));
    }
}
