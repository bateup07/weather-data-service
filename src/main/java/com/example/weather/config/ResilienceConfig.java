package com.example.weather.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.retry.RetryConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.example.weather.exception.UpstreamWeatherException;

import java.time.Duration;

/**
 * Resilience4j settings for upstream weather calls.
 */
@Configuration
public class ResilienceConfig {

    /**
     * Builds the circuit breaker used when the weather provider is failing.
     *
     * @return circuit breaker configuration
     */
    @Bean
    public CircuitBreakerConfig weatherCircuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
                .slidingWindowSize(20)
                .minimumNumberOfCalls(10)
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(20))
                .permittedNumberOfCallsInHalfOpenState(3)
                .build();
    }

    /**
     * Builds the rate limiter that caps outbound weather requests.
     *
     * @return rate limiter configuration
     */
    @Bean
    public RateLimiterConfig weatherRateLimiterConfig() {
        return RateLimiterConfig.custom()
                .limitForPeriod(50)
                .limitRefreshPeriod(Duration.ofSeconds(1))
                .timeoutDuration(Duration.ZERO)
                .build();
    }

    /**
     * Builds the retry policy for transient upstream weather failures.
     *
     * @return retry configuration
     */
    @Bean
    public RetryConfig weatherRetryConfig() {
        return RetryConfig.custom()
                .maxAttempts(2)
                .waitDuration(Duration.ofMillis(200))
                .retryExceptions(UpstreamWeatherException.class)
                .build();
    }
}
