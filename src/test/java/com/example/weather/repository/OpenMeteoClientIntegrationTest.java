package com.example.weather.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OpenMeteoClientIntegrationTest {

    private MockWebServer weatherServer;
    private MockWebServer geocodingServer;
    private OpenMeteoClient client;

    @BeforeEach
    void setUp() throws IOException {
        weatherServer = new MockWebServer();
        geocodingServer = new MockWebServer();
        weatherServer.start();
        geocodingServer.start();
        client = new OpenMeteoClient(
                webClient(weatherServer.url("/").toString()),
                webClient(geocodingServer.url("/").toString()));
    }

    @AfterEach
    void tearDown() throws IOException {
        weatherServer.shutdown();
        geocodingServer.shutdown();
    }

    @Test
    void shouldReadLocationsFromGeocodingApi() {
        geocodingServer.enqueue(json("""
                {"results":[{"name":"London","country_code":"GB","admin1":"England","latitude":51.5,"longitude":-0.12}]}
                """));

        StepVerifier.create(client.findLocations("London"))
                .assertNext(response -> {
                    assertEquals(1, response.results().size());
                    assertEquals("London", response.results().getFirst().name());
                    assertEquals("GB", response.results().getFirst().countryCode());
                })
                .verifyComplete();
    }

    @Test
    void shouldReadWeatherFromForecastApi() {
        weatherServer.enqueue(json("""
                {
                  "current": {
                    "time": "2026-08-25T17:00:00",
                    "temperature_2m": 18.5,
                    "relative_humidity_2m": 75,
                    "weather_code": 61,
                    "wind_speed_10m": 4.2
                  },
                  "daily": {
                    "time": ["2026-08-26"],
                    "weather_code": [0],
                    "temperature_2m_max": [20],
                    "temperature_2m_min": [12],
                    "precipitation_probability_max": [10]
                  }
                }
                """));

        StepVerifier.create(client.getWeather(51.5, -0.12))
                .assertNext(response -> {
                    assertEquals(18.5, response.current().temperature2m());
                    assertEquals(61, response.current().weatherCode());
                    assertEquals(1, response.daily().time().size());
                })
                .verifyComplete();
    }

    @Test
    void shouldFailWhenProviderReturnsAnError() {
        geocodingServer.enqueue(new MockResponse().setResponseCode(500));

        StepVerifier.create(client.findLocations("London"))
                .expectError()
                .verify();
    }

    private static WebClient webClient(final String baseUrl) {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        return WebClient.builder()
                .baseUrl(baseUrl)
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer.defaultCodecs()
                                .jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper)))
                        .build())
                .build();
    }

    private static MockResponse json(final String body) {
        return new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(body);
    }
}
