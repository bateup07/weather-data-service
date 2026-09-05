package com.example.weather.repository;

import com.example.weather.exception.LocationNotFoundException;
import com.example.weather.exception.UpstreamWeatherException;
import com.example.weather.model.LocationData;
import com.example.weather.model.response.OpenMeteoForecastResponse;
import com.example.weather.model.response.OpenMeteoGeocodingResponse;
import com.example.weather.model.WeatherData;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OpenMeteoRepositoryTest {

    private final OpenMeteoClient client = mock(OpenMeteoClient.class);
    private final OpenMeteoMapper mapper = mock(OpenMeteoMapper.class);
    private final OpenMeteoRepository repository = new OpenMeteoRepository(client, mapper);

    @Test
    void shouldMapLocations() {
        var response = new OpenMeteoGeocodingResponse(List.of());
        var locations = List.of(new LocationData("London", "GB", "England", 51.5, -0.1));
        when(client.findLocations("London")).thenReturn(Mono.just(response));
        when(mapper.toLocationData(response, "London")).thenReturn(locations);

        StepVerifier.create(repository.findLocations("London"))
                .assertNext(result -> assertEquals("GB", result.getFirst().country()))
                .verifyComplete();
    }

    @Test
    void shouldTreatEmptyLocationResponseAsNotFound() {
        when(client.findLocations("Atlantis")).thenReturn(Mono.empty());

        StepVerifier.create(repository.findLocations("Atlantis"))
                .expectError(LocationNotFoundException.class)
                .verify();
    }

    @Test
    void shouldWrapUnexpectedLocationErrors() {
        when(client.findLocations("London")).thenReturn(Mono.error(new RuntimeException("timeout")));

        StepVerifier.create(repository.findLocations("London"))
                .expectError(UpstreamWeatherException.class)
                .verify();
    }

    @Test
    void shouldMapWeather() {
        var response = new OpenMeteoForecastResponse(null, null);
        var weather = new WeatherData(18, "Clear sky", 3, 70, LocalDateTime.now(), List.of());
        when(client.getWeather(51.5, -0.1)).thenReturn(Mono.just(response));
        when(mapper.toWeatherData(response)).thenReturn(weather);

        StepVerifier.create(repository.getWeather(51.5, -0.1))
                .assertNext(result -> assertEquals("Clear sky", result.condition()))
                .verifyComplete();
    }

    @Test
    void shouldTreatEmptyWeatherResponseAsUpstreamFailure() {
        when(client.getWeather(51.5, -0.1)).thenReturn(Mono.empty());

        StepVerifier.create(repository.getWeather(51.5, -0.1))
                .expectError(UpstreamWeatherException.class)
                .verify();
    }
}
