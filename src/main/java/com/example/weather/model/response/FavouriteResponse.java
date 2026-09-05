package com.example.weather.model.response;

/**
 * API response for a saved favourite location.
 *
 * @param id       database identifier
 * @param location saved place name
 */
public record FavouriteResponse(
        Long id,
        String location
) {
}
