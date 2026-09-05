package com.example.weather.controller;

import com.example.weather.model.CreateFavouriteRequest;
import com.example.weather.model.response.FavouriteResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureWebTestClient
class FavouriteControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldCreateListAndDeleteFavourite() {
        FavouriteResponse created = webTestClient.post()
                .uri("/api/favourites")
                .bodyValue(new CreateFavouriteRequest("York"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(FavouriteResponse.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(created);
        assertEquals("York", created.location());
        assertNotNull(created.id());

        webTestClient.get()
                .uri("/api/favourites")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[?(@.location == 'York')]").exists();

        webTestClient.delete()
                .uri("/api/favourites/{id}", created.id())
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void shouldRejectBlankFavourite() {
        webTestClient.post()
                .uri("/api/favourites")
                .bodyValue(new CreateFavouriteRequest(" "))
                .exchange()
                .expectStatus().isBadRequest();
    }
}
