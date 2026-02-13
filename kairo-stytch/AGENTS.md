# kairo-stytch

Stytch identity management integration as a Feature. Wraps the Stytch Java SDK with a `Stytch` class that exposes all API surfaces via lazy delegates.

## Key files
- `src/main/kotlin/kairo/stytch/Stytch.kt` -- wrapper class with lazy-delegated API surfaces (users, sessions, magicLinks, etc.)
- `src/main/kotlin/kairo/stytch/StytchResult.kt` -- `StytchResult.get()` extension for unwrapping results
- `feature/src/main/kotlin/kairo/stytch/StytchFeature.kt` -- Feature that creates and binds `Stytch` into Koin

## Submodules
- `:feature` -- `StytchFeature` and config

## Patterns and conventions
- Inject `Stytch` from Koin and call its API surfaces directly (e.g. `stytch.sessions.authenticate(...)`)
- Use `StytchResult.get()` to unwrap Success or throw on Error
- The wrapper exists because `StytchClient` uses `@JvmField`, which breaks mocking in tests

## Foot-guns
- Do not use `StytchClient` directly; the `Stytch` wrapper is necessary for testability

## Related modules
- **kairo-dependency-injection** -- `Stytch` is bound in Koin via `HasKoinModules`
- **kairo-rest** -- Stytch sessions can be used for JWT-based auth via `AuthConfig`
