package com.example.weather.model;

import java.time.LocalDate;

/**
 * Domain model for one day of forecast data.
 *
 * @param date                 forecast date
 * @param highTemperature      maximum temperature
 * @param lowTemperature       minimum temperature
 * @param condition            weather description
 * @param precipitationChance  chance of precipitation
 */
public record DailyWeatherData(
        LocalDate date,
        double highTemperature,
        double lowTemperature,
        String condition,
        int precipitationChance
) {
}