# kairo-sql

SQL database access via Exposed ORM with R2DBC for async I/O. `SqlFeature` manages the R2DBC connection pool and Exposed database.

## Key files
- `feature/src/main/kotlin/kairo/sql/SqlFeature.kt` -- Feature that creates connection pool, database, and Koin bindings
- `feature/src/main/kotlin/kairo/sql/SqlFeatureConfig.kt` -- config data classes for connection factory, pool, and database settings
- `postgres/src/main/kotlin/kairo/sql/postgres/ExceptionMapper.kt` -- maps Postgres constraint violations to domain exceptions

## Submodules
- `:feature` -- `SqlFeature` and config
- `:postgres` -- Postgres-specific utilities (exception mappers for foreign key and unique violations)

## Patterns and conventions
- Configure via `SqlFeatureConfig` with connection URL, credentials, pool sizing, and timeouts
- Koin binds `ConnectionFactory` and `R2dbcDatabase` for injection
- Define Exposed `Table` objects and use `suspendTransaction` for async queries
- `SqlFeature.healthCheck(connectionFactory)` validates a connection for readiness checks
- `withExceptionMappers()` catches constraint violations and maps them to `LogicalFailure`

## Foot-guns
- R2DBC URLs use `r2dbc:` prefix, not `jdbc:`; ensure config URLs match
- Pool defaults: initial=10, min=5, max=25; tune for your workload
- `statementTimeout` defaults to 10s; long queries will be killed

## Related modules
- **kairo-dependency-injection** -- `SqlFeature` implements `HasKoinModules`
- **kairo-health-check** -- use `SqlFeature.healthCheck()` as a readiness check
- **kairo-integration-testing:postgres** -- Testcontainers support for integration tests
- **kairo-protected-string** -- database password is stored as `ProtectedString`
- **kairo-exception** -- exception mappers convert to `LogicalFailure`
