package com.example.weather.service;

import com.example.weather.model.LocationData;
import com.example.weather.repository.WeatherRepository;
import com.example.weather.service.impl.LocationServiceImpl;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LocationServiceImplTest {

    @Test
    void shouldMapLocationResults() {
        var repository = mock(WeatherRepository.class);
        when(repository.findLocations("london"))
                .thenReturn(Mono.just(List.of(
                        new LocationData("London", "GB", "England", 51.5074, -0.1278),
                        new LocationData("London", "CA", "Ontario", 42.9849, -81.2453))));

        var service = new LocationServiceImpl(repository);

        StepVerifier.create(service.search(" london "))
                .assertNext(result -> {
                    assertEquals(2, result.size());
                    assertEquals("GB", result.getFirst().country());
                    assertEquals("CA", result.get(1).country());
                })
                .verifyComplete();
    }
}