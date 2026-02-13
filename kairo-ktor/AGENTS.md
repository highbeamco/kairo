# kairo-ktor

Internal module providing `KairoConverter`, a Ktor `ContentConverter` adapted from Ktor's `JacksonConverter` that preserves full generic type information via kairo-reflect.

## Key files
- `src/main/kotlin/kairo/ktor/KairoConverter.kt` -- content converter that uses `KairoType` instead of erased Java types

## Patterns and conventions
- This module is internal; you should not depend on it directly
- It is pulled in automatically by kairo-rest
- The converter bridges Ktor's content negotiation pipeline with Jackson via `KairoType`

## Related modules
- **kairo-rest** -- the primary consumer of this module
- **kairo-reflect** -- provides `KairoType` for type-safe conversion
- **kairo-serialization** -- `KairoJson` is used for the actual serialization
