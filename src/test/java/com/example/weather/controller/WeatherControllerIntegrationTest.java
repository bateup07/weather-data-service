package com.example.weather.controller;

import com.example.weather.exception.LocationNotFoundException;
import com.example.weather.exception.UpstreamWeatherException;
import com.example.weather.model.response.CurrentWeatherResponse;
import com.example.weather.model.ForecastDay;
import com.example.weather.model.response.ForecastResponse;
import com.example.weather.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureWebTestClient
class WeatherControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private WeatherService weatherService;

    @Test
    void shouldReturnCurrentWeatherForAllMatchingLocations() {
        when(weatherService.getCurrentWeather("London"))
                .thenReturn(Mono.just(List.of(
                        new CurrentWeatherResponse("London", "GB", "England", 51.5074, -0.1278,
                                18.5, "Rain", 4.2, 75,
                                LocalDateTime.parse("2026-08-25T17:00:00")),
                        new CurrentWeatherResponse("London", "CA", "Ontario", 42.9834, -81.2330,
                                21.0, "Clear sky", 3.1, 65,
                                LocalDateTime.parse("2026-08-25T17:00:00")))));

        webTestClient.get().uri(uriBuilder -> uriBuilder
                        .path("/api/weather/current")
                        .queryParam("location", "London")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(2)
                .jsonPath("$[0].country").isEqualTo("GB")
                .jsonPath("$[1].country").isEqualTo("CA");
    }

    @Test
    void shouldRejectBlankLocation() {
        webTestClient.get().uri(uriBuilder -> uriBuilder
                        .path("/api/weather/current")
                        .queryParam("location", "")
                        .build())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldReturnForecastForAllMatchingLocations() {
        var forecast = List.of(
                new ForecastDay(LocalDate.of(2026, 8, 26), 20, 12, "Clear sky", 10),
                new ForecastDay(LocalDate.of(2026, 8, 27), 21, 13, "Rain", 60));

        when(weatherService.getForecast("London", 2))
                .thenReturn(Mono.just(List.of(
                        new ForecastResponse("London", "GB", "England", 51.5074, -0.1278, forecast),
                        new ForecastResponse("London", "CA", "Ontario", 42.9834, -81.2330, forecast))));

        webTestClient.get().uri(uriBuilder -> uriBuilder
                        .path("/api/weather/forecast")
                        .queryParam("location", "London")
                        .queryParam("days", "2")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(2)
                .jsonPath("$[0].forecast.length()").isEqualTo(2)
                .jsonPath("$[1].country").isEqualTo("CA");
    }

    @Test
    void shouldRejectForecastDaysOutsideRange() {
        webTestClient.get().uri(uriBuilder -> uriBuilder
                        .path("/api/weather/forecast")
                        .queryParam("location", "London")
                        .queryParam("days", "6")
                        .build())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldReturnNotFoundWhenLocationIsUnknown() {
        when(weatherService.getCurrentWeather("Nowhere"))
                .thenReturn(Mono.error(new LocationNotFoundException("Nowhere")));

        webTestClient.get().uri(uriBuilder -> uriBuilder
                        .path("/api/weather/current")
                        .queryParam("location", "Nowhere")
                        .build())
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404);
    }

    @Test
    void shouldReturnServiceUnavailableWhenUpstreamFails() {
        when(weatherService.getCurrentWeather("London"))
                .thenReturn(Mono.error(new UpstreamWeatherException("down")));

        webTestClient.get().uri(uriBuilder -> uriBuilder
                        .path("/api/weather/current")
                        .queryParam("location", "London")
                        .build())
                .exchange()
                .expectStatus().isEqualTo(503)
                .expectBody()
                .jsonPath("$.status").isEqualTo(503);
    }
}
