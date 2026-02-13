# kairo-client

Ktor-native outgoing HTTP client. Each external service integration should extend `ClientFeature`.

## Key files
- `feature/src/main/kotlin/kairo/client/ClientFeature.kt` -- abstract base class for HTTP client Features
- `feature/src/main/kotlin/kairo/client/HttpClientFactory.kt` -- creates configured Ktor `HttpClient` instances

## Submodules
- `:feature` -- `ClientFeature` base class and Ktor client factory

## Patterns and conventions
- Extend `ClientFeature` and override `configure()` to customize the Ktor client for each external service
- Each client Feature gets its own named Koin binding (via `httpClientName`)
- Clients include JSON content negotiation with `KairoJson` by default
- Lifecycle: the HTTP client is created lazily, started on Feature start, and closed on stop

## Foot-guns
- The `HttpClient` is created lazily; configuration errors won't surface until first use or startup

## Related modules
- **kairo-feature** -- `ClientFeature` extends `Feature`
- **kairo-dependency-injection** -- clients are registered in Koin with named qualifiers
- **kairo-serialization** -- `KairoJson` is used for client JSON content negotiation
