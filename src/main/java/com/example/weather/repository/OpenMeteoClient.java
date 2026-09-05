package com.example.weather.repository;

import com.example.weather.model.response.OpenMeteoForecastResponse;
import com.example.weather.model.response.OpenMeteoGeocodingResponse;
import com.example.weather.util.OpenMeteoConstants;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * HTTP client for the Open-Meteo geocoding and forecast APIs.
 */
@Component
public class OpenMeteoClient {

    private final WebClient weatherWebClient;
    private final WebClient geocodingWebClient;

    /**
     * Creates the client.
     *
     * @param weatherWebClient   client pointed at the forecast API
     * @param geocodingWebClient client pointed at the geocoding API
     */
    public OpenMeteoClient(@Qualifier("weatherWebClient") final WebClient weatherWebClient, @Qualifier("geocodingWebClient")
            final WebClient geocodingWebClient) {
        this.weatherWebClient = weatherWebClient;
        this.geocodingWebClient = geocodingWebClient;
    }

    /**
     * Searches Open-Meteo geocoding for a place name.
     *
     * @param location city or place name to search
     * @return raw geocoding response
     */
    public Mono<OpenMeteoGeocodingResponse> findLocations(final String location) {
        return geocodingWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(OpenMeteoConstants.GEOCODING_PATH)
                        .queryParam(
                                OpenMeteoConstants.PARAM_NAME,
                                location)
                        .queryParam(
                                OpenMeteoConstants.PARAM_COUNT,
                                OpenMeteoConstants.LOCATION_RESULT_COUNT)
                        .queryParam(
                                OpenMeteoConstants.PARAM_LANGUAGE,
                                OpenMeteoConstants.LANGUAGE_ENGLISH)
                        .queryParam(
                                OpenMeteoConstants.PARAM_FORMAT,
                                OpenMeteoConstants.FORMAT_JSON)
                        .build())
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        response -> response.createException())
                .bodyToMono(OpenMeteoGeocodingResponse.class);
    }

    /**
     * Fetches current weather and a five-day forecast for a coordinate.
     *
     * @param latitude  location latitude
     * @param longitude location longitude
     * @return raw forecast response
     */
    public Mono<OpenMeteoForecastResponse> getWeather(final double latitude, final double longitude) {
        return weatherWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(OpenMeteoConstants.FORECAST_PATH)
                        .queryParam(
                                OpenMeteoConstants.PARAM_LATITUDE,
                                latitude)
                        .queryParam(
                                OpenMeteoConstants.PARAM_LONGITUDE,
                                longitude)
                        .queryParam(
                                OpenMeteoConstants.PARAM_CURRENT,
                                OpenMeteoConstants.CURRENT_WEATHER_FIELDS)
                        .queryParam(
                                OpenMeteoConstants.PARAM_DAILY,
                                OpenMeteoConstants.DAILY_WEATHER_FIELDS)
                        .queryParam(
                                OpenMeteoConstants.PARAM_FORECAST_DAYS,
                                OpenMeteoConstants.FORECAST_DAYS)
                        .queryParam(
                                OpenMeteoConstants.PARAM_TIMEZONE,
                                OpenMeteoConstants.TIMEZONE_AUTO)
                        .build())
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        response -> response.createException())
                .bodyToMono(OpenMeteoForecastResponse.class);
    }
}
