package com.example.weather.util;

/**
 * Paths, query parameters and field lists used by the Open-Meteo client.
 */
public final class OpenMeteoConstants {

    private OpenMeteoConstants() {}

    public static final String GEOCODING_PATH = "/v1/search";
    public static final String FORECAST_PATH = "/v1/forecast";

    public static final String PARAM_NAME = "name";
    public static final String PARAM_COUNT = "count";
    public static final String PARAM_LANGUAGE = "language";
    public static final String PARAM_FORMAT = "format";

    public static final String PARAM_LATITUDE = "latitude";
    public static final String PARAM_LONGITUDE = "longitude";
    public static final String PARAM_CURRENT = "current";
    public static final String PARAM_DAILY = "daily";
    public static final String PARAM_FORECAST_DAYS = "forecast_days";
    public static final String PARAM_TIMEZONE = "timezone";

    public static final String LANGUAGE_ENGLISH = "en";
    public static final String FORMAT_JSON = "json";
    public static final String TIMEZONE_AUTO = "auto";

    public static final int LOCATION_RESULT_COUNT = 10;
    public static final int FORECAST_DAYS = 5;

    public static final String CURRENT_WEATHER_FIELDS =
            "temperature_2m,"
                    + "relative_humidity_2m,"
                    + "weather_code,"
                    + "wind_speed_10m";

    public static final String DAILY_WEATHER_FIELDS =
            "weather_code,"
                    + "temperature_2m_max,"
                    + "temperature_2m_min,"
                    + "precipitation_probability_max";
}
