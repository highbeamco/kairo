# kairo-integration-testing

Black-box integration testing support for Kairo applications. Provides `FeatureTest` which creates a Server for each test with full lifecycle management.

## Key files
- `src/main/kotlin/kairo/feature/FeatureTest.kt` -- JUnit extension that starts/stops a Server per test
- `src/main/kotlin/kairo/feature/FeatureTestAware.kt` -- mixin for accessing the test Server

## Submodules
- `:postgres` -- Testcontainers-based Postgres for integration tests (see `kairo-integration-testing/postgres/AGENTS.md`)

## Patterns and conventions
- Extend `FeatureTest` and implement `createServer()` to define Features under test
- The Server starts in `beforeEach` and stops in `afterEach`
- Koin bindings are available for parameter injection in test methods
- Use `context.server` to access the running Server instance

## Foot-guns
- Each test gets a fresh Server; shared state between tests requires explicit setup
- The Server must start successfully or the test will fail in `beforeEach`

## Related modules
- **kairo-server** -- `Server` is created and managed by `FeatureTest`
- **kairo-testing** -- unit test utilities (separate from integration testing)
- **kairo-dependency-injection** -- `KoinExtension` base class provides DI in tests
