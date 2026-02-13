# kairo-datetime

Extends kotlinx-datetime with convenience extensions.

## Key files
- `src/main/kotlin/kairo/datetime/Instant.kt` -- `Instant.epoch` extension property (Unix epoch shorthand)

## Patterns and conventions
- Re-exports kotlinx-datetime types so consumers don't need a separate dependency
- `Instant.epoch` is useful in tests for deterministic timestamps
