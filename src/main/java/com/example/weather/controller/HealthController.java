package com.example.weather.controller;

import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * HTTP API for a simple application health check.
 */
@RestController
public class HealthController {

    private final HealthEndpoint healthEndpoint;

    /**
     * Creates the controller.
     *
     * @param healthEndpoint Actuator health endpoint used to report status
     */
    public HealthController(final HealthEndpoint healthEndpoint) {
        this.healthEndpoint = healthEndpoint;
    }

    /**
     * Returns the current application health status.
     *
     * @return Actuator health component
     */
    @GetMapping("/api/health")
    public HealthComponent health() {
        return healthEndpoint.health();
    }
}
