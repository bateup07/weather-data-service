package com.example.weather.model;

import jakarta.validation.constraints.NotBlank;

/**
 * Request body used to save a favourite location.
 *
 * @param location place name to store
 */
public record CreateFavouriteRequest(
        @NotBlank(message = "Location must not be blank")
        String location) {
}