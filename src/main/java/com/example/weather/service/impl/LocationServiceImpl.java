package com.example.weather.service.impl;

import com.example.weather.model.LocationData;
import com.example.weather.model.response.LocationResponse;
import com.example.weather.repository.WeatherRepository;
import com.example.weather.service.LocationService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Converts geocoding results into API location responses.
 */
@Service
public class LocationServiceImpl implements LocationService {

    private final WeatherRepository repository;

    /**
     * Creates the service.
     *
     * @param repository source of location data
     */
    public LocationServiceImpl(final WeatherRepository repository) {
        this.repository = repository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "locationSearch", key = "#location.trim().toLowerCase()")
    public Mono<List<LocationResponse>> search(final String location) {
        return repository.findLocations(location.trim())
                .map(locations -> locations.stream()
                        .map(this::toResponse)
                        .toList());
    }

    private LocationResponse toResponse(final LocationData location) {
        return new LocationResponse(
                location.name(),
                location.country(),
                location.state(),
                location.latitude(),
                location.longitude()
        );
    }
}
