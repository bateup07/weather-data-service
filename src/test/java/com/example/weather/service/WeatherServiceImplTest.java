package com.example.weather.service;

import com.example.weather.exception.InvalidRequestException;
import com.example.weather.model.DailyWeatherData;
import com.example.weather.model.LocationData;
import com.example.weather.model.WeatherData;
import com.example.weather.repository.WeatherRepository;
import com.example.weather.service.impl.WeatherServiceImpl;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;

class WeatherServiceImplTest {

    private final WeatherRepository repository = mock(WeatherRepository.class);
    private final WeatherService service = new WeatherServiceImpl(repository);

    @Test
    void shouldReturnWeatherForEveryMatchingLocationConcurrently() {
        var londonGb = new LocationData("London", "GB", "England", 51.5074, -0.1278);
        var londonCa = new LocationData("London", "CA", "Ontario", 42.9834, -81.2330);

        when(repository.findLocations("London"))
                .thenReturn(Mono.just(List.of(londonGb, londonCa)));
        when(repository.getWeather(anyDouble(), anyDouble()))
                .thenAnswer(invocation -> Mono.just(new WeatherData(
                        18.5, "Rain", 4.2, 75,
                        LocalDateTime.parse("2026-08-25T17:00:00"),
                        List.of())));

        StepVerifier.create(service.getCurrentWeather("London"))
                .assertNext(results -> {
                    assertEquals(2, results.size());
                    assertEquals("GB", results.get(0).country());
                    assertEquals("CA", results.get(1).country());
                })
                .verifyComplete();

        verify(repository).findLocations("London");
        verify(repository, times(2)).getWeather(anyDouble(), anyDouble());
    }

    @Test
    void shouldReturnForecastForEveryMatchingLocation() {
        var londonGb = new LocationData("London", "GB", "England", 51.5074, -0.1278);
        var londonCa = new LocationData("London", "CA", "Ontario", 42.9834, -81.2330);
        var daily = List.of(
                new DailyWeatherData(LocalDate.of(2026, 8, 26), 20, 12, "Clear sky", 10),
                new DailyWeatherData(LocalDate.of(2026, 8, 27), 21, 13, "Rain", 60),
                new DailyWeatherData(LocalDate.of(2026, 8, 28), 19, 11, "Cloudy", 30));

        when(repository.findLocations("London"))
                .thenReturn(Mono.just(List.of(londonGb, londonCa)));
        when(repository.getWeather(anyDouble(), anyDouble()))
                .thenReturn(Mono.just(new WeatherData(
                        18, "Clear sky", 3, 70, LocalDateTime.now(), daily)));

        StepVerifier.create(service.getForecast("London", 2))
                .assertNext(results -> {
                    assertEquals(2, results.size());
                    assertEquals("GB", results.get(0).country());
                    assertEquals(2, results.get(0).forecast().size());
                    assertEquals("CA", results.get(1).country());
                })
                .verifyComplete();
    }

    @Test
    void shouldRejectForecastGreaterThanFiveDays() {
        StepVerifier.create(service.getForecast("Manchester", 6))
                .expectError(InvalidRequestException.class)
                .verify();

        verifyNoInteractions(repository);
    }

    @Test
    void shouldRejectForecastFewerThanOneDay() {
        StepVerifier.create(service.getForecast("Manchester", 0))
                .expectError(InvalidRequestException.class)
                .verify();

        verifyNoInteractions(repository);
    }

    @Test
    void shouldStartMultipleWeatherRequestsWithoutWaitingForPreviousRequest() {
        var locations = List.of(
                new LocationData("London", "GB", "England", 51.5074, -0.1278),
                new LocationData("London", "CA", "Ontario", 42.9834, -81.2330),
                new LocationData("London", "US", "Kentucky", 37.1289, -84.0833));
        when(repository.findLocations("London")).thenReturn(Mono.just(locations));

        AtomicInteger active = new AtomicInteger();
        AtomicInteger maxActive = new AtomicInteger();
        when(repository.getWeather(anyDouble(), anyDouble())).thenAnswer(invocation ->
                Mono.defer(() -> {
                    int current = active.incrementAndGet();
                    maxActive.updateAndGet(previous -> Math.max(previous, current));
                    return Mono.just(new WeatherData(
                                    18, "Clear sky", 3, 70, LocalDateTime.now(), List.of()))
                            .delayElement(java.time.Duration.ofMillis(50))
                            .doFinally(signal -> active.decrementAndGet());
                }));

        StepVerifier.create(service.getCurrentWeather("London"))
                .assertNext(results -> assertEquals(3, results.size()))
                .verifyComplete();

        // More than one request being active proves the service is not sequential.
        org.junit.jupiter.api.Assertions.assertTrue(maxActive.get() > 1);
    }
}
