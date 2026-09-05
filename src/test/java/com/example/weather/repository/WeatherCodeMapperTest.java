package com.example.weather.repository;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WeatherCodeMapperTest {

    private final WeatherCodeMapper mapper = new WeatherCodeMapper();

    @Test
    void shouldDescribeKnownAndUnknownCodes() {
        assertEquals("Clear sky", mapper.toDescription(0));
        assertEquals("Rain", mapper.toDescription(61));
        assertEquals("Thunderstorm", mapper.toDescription(95));
        assertEquals("Unknown", mapper.toDescription(123));
    }
}
