package com.example.weather.repository;

import com.example.weather.exception.LocationNotFoundException;
import com.example.weather.exception.UpstreamWeatherException;
import com.example.weather.model.*;
import com.example.weather.model.response.OpenMeteoForecastResponse;
import com.example.weather.model.response.OpenMeteoGeocodingResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Converts Open-Meteo API payloads into domain models.
 */
@Component
public class OpenMeteoMapper {

    private final WeatherCodeMapper weatherCodeMapper;

    /**
     * Creates the mapper.
     *
     * @param weatherCodeMapper converts WMO weather codes to descriptions
     */
    public OpenMeteoMapper(final WeatherCodeMapper weatherCodeMapper) {
        this.weatherCodeMapper = weatherCodeMapper;
    }

    /**
     * Maps a geocoding response to domain locations.
     *
     * @param response          Open-Meteo geocoding payload
     * @param requestedLocation original search text, used in not-found errors
     * @return matching locations
     * @throws LocationNotFoundException if the provider returned no results
     */
    public List<LocationData> toLocationData(final OpenMeteoGeocodingResponse response, final String requestedLocation) {
        if (response.results() == null || response.results().isEmpty()) {
            throw new LocationNotFoundException(requestedLocation);
        }
        return response.results()
                .stream()
                .map(location -> new LocationData(
                        location.name(),
                        location.countryCode(),
                        location.admin1(),
                        location.latitude(),
                        location.longitude()))
                .toList();
    }

    /**
     * Maps a forecast response to domain weather data.
     *
     * @param response Open-Meteo forecast payload
     * @return current weather and daily forecast
     * @throws UpstreamWeatherException if current weather is missing or daily data is incomplete
     */
    public WeatherData toWeatherData(final OpenMeteoForecastResponse response) {
        if (response.current() == null) {
            throw new UpstreamWeatherException(
                    "Weather provider returned no current weather data");
        }
        return new WeatherData(
                response.current().temperature2m(),
                weatherCodeMapper.toDescription(
                        response.current().weatherCode()),
                response.current().windSpeed10m(),
                response.current().relativeHumidity2m(),
                response.current().time(),
                toDailyWeather(response));
    }

    private List<DailyWeatherData> toDailyWeather(final OpenMeteoForecastResponse response) {
        if (response.daily() == null || response.daily().time() == null || response.daily().time().isEmpty()) {
            return List.of();
        }
        return IntStream.range(0, response.daily().time().size())
                .mapToObj(index -> toDailyWeather(
                        response,
                        index))
                .toList();
    }

    private DailyWeatherData toDailyWeather(final OpenMeteoForecastResponse response, final int index) {
        final var daily = response.daily();

        return new DailyWeatherData(
                LocalDate.parse(daily.time().get(index)),
                valueAt(daily.temperature2mMax(), index),
                valueAt(daily.temperature2mMin(), index),
                weatherCodeMapper.toDescription(
                        (int) valueAt(
                                daily.weatherCode(),
                                index)),
                (int) valueAt(
                        daily.precipitationProbabilityMax(),
                        index));
    }

    private double valueAt(final List<Double> values, final int index) {
        if (values == null || index >= values.size() || values.get(index) == null) {
            throw new UpstreamWeatherException(
                    "Weather provider returned incomplete daily weather data");
        }
        return values.get(index);
    }
}
