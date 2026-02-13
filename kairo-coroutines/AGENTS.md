# kairo-coroutines

Extends Kotlin coroutines using Arrow's coroutines library and adds convenient helper functions.

## Key files
- `src/main/kotlin/kairo/coroutines/ParZip.kt` -- parallel zip functions for concurrent coroutine execution
- `src/main/kotlin/kotlin/collections/Flow.kt` -- `singleNullOrThrow()` for Flows

## Patterns and conventions
- `singleNullOrThrow()` is preferred over `singleOrNull()` throughout the codebase because `singleOrNull()` silently returns null for multiple matches
- `parZip` functions run multiple suspending operations concurrently and combine results
- Flow extensions are placed in the `kotlin.collections` package to match stdlib conventions

## Related modules
- **kairo-util** -- provides the same `singleNullOrThrow()` pattern for Iterables, Sequences, and Arrays
