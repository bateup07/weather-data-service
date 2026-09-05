package com.example.weather.controller;

import com.example.weather.model.response.CurrentWeatherResponse;
import com.example.weather.model.response.ForecastResponse;
import com.example.weather.service.WeatherService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * HTTP API for current weather and short-range forecasts.
 */
@RestController
@RequestMapping("/api/weather")
@Validated
public class WeatherController {

    private final WeatherService weatherService;

    /**
     * Creates the controller.
     *
     * @param weatherService service used to look up weather
     */
    public WeatherController(final WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    /**
     * Returns current weather for every location matching the given name.
     *
     * @param location city or place name to look up
     * @return current weather for each matching location
     */
    @GetMapping("/current")
    public Mono<List<CurrentWeatherResponse>> currentWeather(@RequestParam("location") @NotBlank final String location) {
        return weatherService.getCurrentWeather(location);
    }

    /**
     * Returns a daily forecast for every location matching the given name.
     *
     * @param location city or place name to look up
     * @param days     number of forecast days, from 1 to 5
     * @return forecast for each matching location
     */
    @GetMapping("/forecast")
    public Mono<List<ForecastResponse>> forecast(@RequestParam("location") @NotBlank final String location,
            @RequestParam(name = "days", defaultValue = "5") @Min(1) @Max(5) final int days) {
        return weatherService.getForecast(location, days);
    }
}