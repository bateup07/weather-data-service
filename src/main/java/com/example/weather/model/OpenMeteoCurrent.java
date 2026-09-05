package com.example.weather.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * Current conditions in an Open-Meteo forecast response.
 *
 * @param time               observation time
 * @param temperature2m      temperature at 2 metres
 * @param relativeHumidity2m relative humidity at 2 metres
 * @param weatherCode        WMO weather code
 * @param windSpeed10m       wind speed at 10 metres
 */
public record OpenMeteoCurrent(

        LocalDateTime time,

        @JsonProperty("temperature_2m")
        double temperature2m,

        @JsonProperty("relative_humidity_2m")
        int relativeHumidity2m,

        @JsonProperty("weather_code")
        int weatherCode,

        @JsonProperty("wind_speed_10m")
        double windSpeed10m) {
}
