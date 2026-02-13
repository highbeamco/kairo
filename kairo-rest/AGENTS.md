# kairo-rest

REST layer built on Ktor. Provides a type-safe endpoint DSL where API contracts are defined as data classes annotated with `@Rest`, extending `RestEndpoint<I, O>`.

## Key files
- `endpoint/src/main/kotlin/kairo/rest/RestEndpoint.kt` -- base class for endpoint definitions with `@PathParam` and `@QueryParam`
- `endpoint/src/main/kotlin/kairo/rest/Rest.kt` -- `@Rest(method, path)` annotation for endpoints
- `src/main/kotlin/kairo/rest/RestEndpointHandler.kt` -- handler DSL with `auth {}`, `handle {}`, `statusCode {}` blocks
- `src/main/kotlin/kairo/rest/auth/AuthReceiver.kt` -- authentication context with `public()` and `verify()` methods
- `feature/src/main/kotlin/kairo/rest/RestFeature.kt` -- the Feature that runs the Ktor server at priority 100,000,000

## Submodules
- `:endpoint` -- endpoint definitions, `@Rest` annotation, template parsing
- `:feature` -- `RestFeature`, Ktor server factory, exception mapping
- `:serialization` -- JSON serialization utilities for REST (RestModule for HttpMethod/HttpStatusCode)

## Patterns and conventions
- Define API as data classes: `@Rest("GET", "/books/:bookId") data class GetBook(@PathParam val bookId: String) : RestEndpoint<Unit, BookRep>()`
- Implement `HasRouting` on your Feature and use `route(GetBook::class) { handle { ... } }` in `routing()`
- Auth is optional; configure via `AuthConfig` passed to `RestFeature`
- Logical failures (`LogicalFailure`) auto-map to HTTP error responses
- Path parameters use `:paramName` syntax (not `{paramName}`)

## Foot-guns
- Endpoint classes must be data classes or data objects; regular classes will fail at template parsing
- Path parameters use `:paramName` syntax, not Ktor's `{paramName}`
- `RestFeature` runs at priority 100,000,000; ensure DI and SQL are started before REST

## Related modules
- **kairo-ktor** -- `KairoConverter` handles JSON content negotiation
- **kairo-feature** -- `HasRouting` interface and `FeaturePriority.rest`
- **kairo-health-check** -- health endpoints are built on this REST layer
- **kairo-serialization** -- `KairoJson` for Jackson configuration
- **kairo-exception** -- `LogicalFailure` is mapped to HTTP error responses
