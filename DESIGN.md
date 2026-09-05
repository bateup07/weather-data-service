# Design Notes

## Technology

Java 21 and Spring Boot are used because the application is a conventional REST API and Java/Spring provides mature support for validation, persistence, caching, testing and resilience.

WebClient is used exclusively for external HTTP calls. The application remains Spring MVC at the API boundary, while WebClient provides a single modern client abstraction for upstream calls.

## Layering

Controllers handle HTTP concerns only. Services contain business rules and transformations. Repositories encapsulate database and external API access.

This keeps the service independent of Open-Meteo, and makes the business logic unit-testable.

## Resilience

The upstream client has connection and response timeouts. Resilience4j provides:
- circuit breaker
- rate limiter
- retry

Caffeine caches current weather, forecasts and location searches to reduce upstream traffic and improve latency.

## Failure behaviour

Invalid requests return HTTP 400. Unknown locations return HTTP 404. Upstream failures return HTTP 503. Rate-limit exhaustion returns HTTP 429.

## Persistence

H2 is the default local database. The datasource can be switched to PostgreSQL using environment variables.

## Testing

Unit tests cover business logic and mapping. Integration tests cover controller routes and the WebClient repository against a deterministic MockWebServer.

## Further improvements

With more time I would add PostgreSQL Testcontainers, distributed caching for multiple service instances, metrics dashboards, structured logging/correlation IDs and contract tests against the upstream provider.

## Reactive external I/O

The weather and geocoding integrations use WebClient without blocking on the HTTP response. Matching locations are converted to weather requests with Reactor `flatMap`, bounded to five concurrent upstream weather requests. This is preferable to a sequential stream because each weather lookup is independent. The bound also works with the configured Resilience4j rate limiter so a large number of matching locations cannot create an unbounded request fan-out.

JPA-backed favourite operations remain a blocking persistence boundary; they are intentionally isolated in the repository/service layers rather than used in the external weather I/O pipeline.
