# kairo-integration-testing:postgres

Testcontainers-based Postgres for integration tests. Creates a single Postgres container per test class and a separate database per test for parallel execution safety.

## Key files
- `src/main/kotlin/kairo/sql/PostgresExtension.kt` -- JUnit extension that manages the Postgres container and per-test databases
- `src/main/kotlin/kairo/sql/PostgresExtensionAware.kt` -- interface providing `connectionFactory`, `database`, `databaseName`, and `postgres` properties

## Patterns and conventions
- Register `PostgresExtension` as a JUnit extension in your `FeatureTest`
- Implement `PostgresExtensionAware` to access test-scoped database properties
- Each test gets a unique database (UUID-named); created in `beforeEach`, dropped in `afterEach`
- `context.connectionFactory` provides an `SqlFeatureConfig.ConnectionFactory` with R2DBC URL for the test database
- `context.database` provides a JDBC `Database` for schema setup (DDL)
- Use `SqlFeature.from(context.connectionFactory!!)` to create an `SqlFeature` configured for testing (smaller pool)
- The Postgres container is shared across all tests in a JVM via the root extension context

## Foot-guns
- Schema initialization must be done manually in `beforeEach` after the database is created
- The JDBC `Database` is for schema setup only; the `SqlFeature` uses R2DBC at runtime

## Related modules
- **kairo-integration-testing** -- parent module; `FeatureTest` base class
- **kairo-sql** -- `SqlFeature` and `SqlFeatureConfig` are configured from the test extension
