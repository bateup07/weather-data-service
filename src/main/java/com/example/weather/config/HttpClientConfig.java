package com.example.weather.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * HTTP clients used to call Open-Meteo.
 */
@Configuration
public class HttpClientConfig {

    /**
     * Builds a Netty client with connect and response timeouts.
     *
     * @param connectTimeoutMs  connection timeout in milliseconds
     * @param responseTimeoutMs response timeout in milliseconds
     * @return configured HTTP client
     */
    @Bean
    public HttpClient weatherHttpClient(
            @Value("${weather.upstream.connect-timeout-ms:500}") int connectTimeoutMs,
            @Value("${weather.upstream.response-timeout-ms:1500}") int responseTimeoutMs) {

        return HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeoutMs)
                .responseTimeout(Duration.ofMillis(responseTimeoutMs))
                .doOnConnected(connection -> connection
                        .addHandlerLast(new ReadTimeoutHandler(responseTimeoutMs, TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(responseTimeoutMs, TimeUnit.MILLISECONDS)));
    }

    /**
     * Builds the WebClient for the Open-Meteo forecast API.
     *
     * @param builder           shared WebClient builder
     * @param baseUrl           forecast API base URL
     * @param weatherHttpClient timed Netty client
     * @return forecast WebClient
     */
    @Bean
    @Qualifier("weatherWebClient")
    public WebClient weatherWebClient(
            WebClient.Builder builder,
            @Value("${weather.upstream.base-url}") String baseUrl,
            HttpClient weatherHttpClient) {

        return builder
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(weatherHttpClient))
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    /**
     * Builds the WebClient for the Open-Meteo geocoding API.
     *
     * @param builder           shared WebClient builder
     * @param baseUrl           geocoding API base URL
     * @param weatherHttpClient timed Netty client
     * @return geocoding WebClient
     */
    @Bean
    @Qualifier("geocodingWebClient")
    public WebClient geocodingWebClient(
            WebClient.Builder builder,
            @Value("${weather.geocoding.base-url}") String baseUrl,
            HttpClient weatherHttpClient) {

        return builder
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(weatherHttpClient))
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
