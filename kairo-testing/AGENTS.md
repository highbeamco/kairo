# kairo-testing

Bundles common test dependencies (Kotest assertions, MockK, kotlinx-coroutines-test) and provides semantic test helper functions for readable test structure.

## Key files
- `src/main/kotlin/kairo/testing/Testing.kt` -- `setup {}`, `precondition {}`, `test {}`, `postcondition {}` semantic wrappers

## Patterns and conventions
- `setup {}`, `precondition {}`, `test {}`, `postcondition {}` are no-op wrappers that accept an optional description string; they exist purely for readability
- Tests run concurrently by default with a 10-second timeout
- This module is for unit testing; for integration testing use kairo-integration-testing instead

## Foot-guns
- The semantic wrappers (`setup`, `test`, etc.) do not enforce ordering or add any behavior; they are purely cosmetic
- This module does not include JUnit itself as an API dependency; JUnit comes transitively

## Related modules
- **kairo-exception/testing** -- provides `shouldThrow` for `LogicalFailure` assertions
- **kairo-integration-testing** -- for integration (black-box) testing with a running Server
