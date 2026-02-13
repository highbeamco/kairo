# kairo-id

Human-readable semantic identifiers with variable entropy. Provides compile-time type safety without runtime overhead.

## Key files
- `src/main/kotlin/kairo/id/KairoId.kt` -- base class for typed IDs with prefix and entropy configuration
- `src/main/kotlin/kairo/id/KairoIdModule.kt` -- Jackson module for ID serialization

## Patterns and conventions
- Define ID types as `data class UserId(override val value: String) : KairoId()`
- IDs have a human-readable prefix (e.g. `usr_`) followed by random characters
- Entropy (character count) is configurable per ID type
- Register `KairoIdModule` with your `KairoJson` instance

## Foot-guns
- Forgetting to register `KairoIdModule` will cause Jackson serialization failures
- ID prefixes must be unique across your application to avoid ambiguity
- The `value` property includes the prefix; don't add the prefix manually

## Related modules
- **kairo-serialization** -- `KairoIdModule` must be registered with `KairoJson`
