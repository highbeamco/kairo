# kairo-exception

Differentiates between logical failures (expected domain errors) and real exceptions (unexpected bugs). `LogicalFailure` is the base class for all domain errors and maps cleanly to HTTP status codes.

## Key files
- `src/main/kotlin/kairo/exception/LogicalFailure.kt` -- abstract base class with `type` and `status` properties
- `testing/src/main/kotlin/kairo/exception/shouldThrow.kt` -- test assertion for `LogicalFailure`

## Submodules
- `:testing` -- provides `shouldThrow` for asserting specific `LogicalFailure` types in tests

## Patterns and conventions
- Extend `LogicalFailure` for domain errors (e.g. `UserNotFound`, `InsufficientPermissions`)
- Each subclass defines a `type` string and HTTP `status` code
- Serialized per RFC 9457 (Problem Details for HTTP APIs)
- Real exceptions (bugs) should remain as standard Kotlin exceptions

## Foot-guns
- `LogicalFailure` extends `Exception`, not `Error`; catch blocks should account for this
- The `testing` submodule depends on `kairo-testing`

## Related modules
- **kairo-rest** -- maps `LogicalFailure` to HTTP error responses automatically
