package com.example.weather.model;

import java.time.LocalDate;

/**
 * One day of a forecast.
 *
 * @param date                 forecast date
 * @param highTemperature      maximum temperature
 * @param lowTemperature       minimum temperature
 * @param condition            weather description
 * @param precipitationChance  chance of precipitation
 */
public record ForecastDay(
        LocalDate date,
        double highTemperature,
        double lowTemperature,
        String condition,
        int precipitationChance
) {
}
