package com.example.weather.repository;

import com.example.weather.exception.LocationNotFoundException;
import com.example.weather.exception.UpstreamWeatherException;
import com.example.weather.model.OpenMeteoCurrent;
import com.example.weather.model.OpenMeteoDaily;
import com.example.weather.model.response.OpenMeteoForecastResponse;
import com.example.weather.model.response.OpenMeteoGeocodingResponse;
import com.example.weather.model.OpenMeteoLocation;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OpenMeteoMapperTest {

    private final OpenMeteoMapper mapper = new OpenMeteoMapper(new WeatherCodeMapper());

    @Test
    void shouldMapLocations() {
        var response = new OpenMeteoGeocodingResponse(List.of(
                new OpenMeteoLocation("London", "GB", "England", 51.5074, -0.1278)));

        var result = mapper.toLocationData(response, "London");

        assertEquals(1, result.size());
        assertEquals("GB", result.getFirst().country());
        assertEquals("England", result.getFirst().state());
    }

    @Test
    void shouldRejectEmptyLocations() {
        assertThrows(LocationNotFoundException.class,
                () -> mapper.toLocationData(new OpenMeteoGeocodingResponse(List.of()), "Atlantis"));
    }

    @Test
    void shouldMapWeatherAndDailyForecast() {
        var current = new OpenMeteoCurrent(
                LocalDateTime.parse("2026-08-25T17:00:00"), 18.5, 75, 61, 4.2);
        var daily = new OpenMeteoDaily(
                List.of("2026-08-26"),
                List.of(0.0),
                List.of(20.0),
                List.of(12.0),
                List.of(10.0));

        var result = mapper.toWeatherData(new OpenMeteoForecastResponse(current, daily));

        assertEquals(18.5, result.temperature());
        assertEquals("Rain", result.condition());
        assertEquals(1, result.daily().size());
        assertEquals(LocalDate.of(2026, 8, 26), result.daily().getFirst().date());
        assertEquals("Clear sky", result.daily().getFirst().condition());
    }

    @Test
    void shouldReturnEmptyDailyWhenForecastIsMissing() {
        var current = new OpenMeteoCurrent(
                LocalDateTime.parse("2026-08-25T17:00:00"), 18.5, 75, 0, 4.2);

        var result = mapper.toWeatherData(new OpenMeteoForecastResponse(current, null));

        assertTrue(result.daily().isEmpty());
    }

    @Test
    void shouldRejectMissingCurrentWeather() {
        assertThrows(UpstreamWeatherException.class,
                () -> mapper.toWeatherData(new OpenMeteoForecastResponse(null, null)));
    }
}
