package com.example.weather.controller;

import com.example.weather.model.response.LocationResponse;
import com.example.weather.service.LocationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureWebTestClient
class LocationControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private LocationService locationService;

    @Test
    void shouldReturnMatchingLocations() {
        when(locationService.search("London"))
                .thenReturn(Mono.just(List.of(
                        new LocationResponse("London", "GB", "England", 51.5074, -0.1278),
                        new LocationResponse("London", "CA", "Ontario", 42.9834, -81.2330))));

        webTestClient.get().uri(uriBuilder -> uriBuilder
                        .path("/api/locations/search")
                        .queryParam("query", "London")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(2)
                .jsonPath("$[0].country").isEqualTo("GB")
                .jsonPath("$[1].country").isEqualTo("CA");
    }

    @Test
    void shouldRejectBlankQuery() {
        webTestClient.get().uri(uriBuilder -> uriBuilder
                        .path("/api/locations/search")
                        .queryParam("query", "")
                        .build())
                .exchange()
                .expectStatus().isBadRequest();
    }
}
