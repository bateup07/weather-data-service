package com.example.weather.controller;

import com.example.weather.model.response.LocationResponse;
import com.example.weather.service.LocationService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * HTTP API for geocoding searches.
 */
@RestController
@RequestMapping("/api/locations")
@Validated
public class LocationController {

    private final LocationService locationService;

    /**
     * Creates the controller.
     *
     * @param locationService service used to search locations
     */
    public LocationController(final LocationService locationService) {
        this.locationService = locationService;
    }

    /**
     * Finds locations whose names match the query.
     *
     * @param query city or place name to search
     * @return matching locations
     */
    @GetMapping("/search")
    public Mono<List<LocationResponse>> search(@RequestParam("query") @NotBlank final String query) {
        return locationService.search(query);
    }
}