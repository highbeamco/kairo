# kairo-health-check

Adds health check endpoints (`/health/startup`, `/health/liveness`, `/health/readiness`) to your Kairo application.

## Key files
- `feature/src/main/kotlin/kairo/healthCheck/HealthCheckFeature.kt` -- Feature that registers health check routes at priority 200M

## Submodules
- `:feature` -- `HealthCheckFeature` and route configuration

## Patterns and conventions
- Add `HealthCheckFeature(checks)` to your Server's Feature list
- Provide readiness checks (e.g. `SqlFeature.healthCheck(connectionFactory)`) to validate dependencies
- Health check runs at priority 200,000,000 (after REST)
- Liveness always returns 200; readiness runs the provided checks

## Related modules
- **kairo-rest** -- health check routes are built on the REST layer
- **kairo-sql** -- provides `SqlFeature.healthCheck()` for database readiness
