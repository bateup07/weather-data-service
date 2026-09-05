# Weather Data Service

Spring Boot 3 / Java 21 weather service using WebClient for all external HTTP calls.

## Architecture

- `controller` - HTTP endpoints only
- `service` - business logic
- `repository` - database and external API access
- `model` - API/domain/persistence models
- `config` - infrastructure configuration
- `exception` - API error handling

## Endpoints

- `GET /api/weather/current?location=Manchester`
- `GET /api/weather/forecast?location=Manchester&days=5`
- `GET /api/locations/search?query=London`
- `POST /api/favourites`
- `GET /api/favourites`
- `DELETE /api/favourites/{id}`
- `GET /api/health`
- `GET /actuator/health`

## Run

Requires Java 21 and Maven.

```bash
mvn clean test
mvn spring-boot:run
```

No API key is required because the default provider is Open-Meteo.

## Configuration

Environment variables are documented in `.env.example`.

## Testing

The test suite contains:
- service unit tests with Mockito
- controller integration tests with Spring Boot + MockMvc
- upstream repository integration tests using MockWebServer

## Reactive concurrency

Weather lookups use WebClient and Reactor end-to-end. A partial location such as `London` can resolve to multiple locations; each matching location is mapped to an independent weather request using `Flux.flatMap` with bounded concurrency (5). This avoids sequential upstream calls while preventing unbounded load on the weather provider.

The weather endpoints therefore return `Mono<List<...>>`, allowing Spring WebFlux to keep the request pipeline non-blocking while the external HTTP requests are in flight.
