package com.example.weather.service;

import com.example.weather.model.response.LocationResponse;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Searches for geographic locations by name.
 */
public interface LocationService {
    /**
     * Finds locations whose names match the given value.
     *
     * @param location city or place name to search
     * @return matching locations
     */
    Mono<List<LocationResponse>> search(String location);
}
