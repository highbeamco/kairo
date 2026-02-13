# kairo-feature

The core building block of Kairo applications. Features encapsulate a domain or framework concern, including lifecycle hooks, DI bindings, and REST routing.

## Key files
- `src/main/kotlin/kairo/feature/Feature.kt` -- abstract base class; every Feature extends this
- `src/main/kotlin/kairo/feature/FeaturePriority.kt` -- priority constants for lifecycle ordering
- `src/main/kotlin/kairo/feature/LifecycleHandler.kt` -- start/stop handlers with priority
- `src/main/kotlin/kairo/feature/LifecycleBuilder.kt` -- DSL for defining lifecycle handlers
- `src/main/kotlin/kairo/feature/Lifecycle.kt` -- `lifecycle {}` DSL entrypoint

## Patterns and conventions
- Two types: Framework Features (infrastructure like REST, DI, SQL) and Domain Features (business logic like User, Order)
- Override `lifecycle` to define start/stop behavior at specific priorities
- Standard priorities: DI (-100M) -> Default (0) -> REST (100M) -> HealthCheck (200M)
- Handlers at the same priority run in parallel
- Features expose capabilities via interfaces: `HasKoinModules`, `HasRouting`

## Foot-guns
- Handlers at the same priority run concurrently; they must be thread/coroutine-safe
- Start uses `coroutineScope` (fail-fast); stop uses `supervisorScope` (best-effort)
- Don't put slow operations in a handler without choosing the appropriate priority

## Related modules
- **kairo-server** -- Server composes and manages Features
- **kairo-dependency-injection** -- `HasKoinModules` interface
- **kairo-rest** -- `HasRouting` interface
