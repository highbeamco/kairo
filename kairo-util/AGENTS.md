# kairo-util

Small collection of general-purpose utility functions. Keep this module focused and minimal.

## Key files
- `src/main/kotlin/kairo/util/String.kt` -- `canonicalize()` (normalize to lowercase Latin) and `slugify()`
- `src/main/kotlin/kairo/util/Resources.kt` -- `resource()` wrapper around Guava's `Resources.getResource()`
- `src/main/kotlin/kairo/util/Throwable.kt` -- `Throwable.firstCauseOf<T>()` walks the cause chain
- `src/main/kotlin/kotlin/collections/Iterable.kt` -- `singleNullOrThrow()` for Iterables

## Patterns and conventions
- `singleNullOrThrow()` is preferred over `singleOrNull()` throughout the codebase because `singleOrNull()` silently returns null for multiple matches
- `canonicalize()` uses Unicode NFD normalization, strips non-Latin characters, and collapses whitespace
- Collection extensions are placed in the `kotlin.collections` package to match stdlib conventions

## Foot-guns
- `resource()` requires Guava on the classpath at runtime
- `canonicalize()` strips ALL non-Latin, non-digit characters (CJK, Cyrillic, Arabic, etc.)

## Related modules
- **kairo-coroutines** -- provides the same `singleNullOrThrow()` pattern for Flows
