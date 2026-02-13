# kairo-optional

`Optional<T>` and `Required<T>` types for distinguishing missing, null, and present values in JSON. Critical for implementing RFC 7396 JSON Merge Patch semantics.

## Key files
- `src/main/kotlin/kairo/optional/Optional.kt` -- sealed class with `Missing`, `Null`, and `Value<T>` variants; plus `ifSpecified` and `transform` extensions
- `src/main/kotlin/kairo/optional/Required.kt` -- sealed class with `Missing` and `Value<T>` variants (no null)
- `src/main/kotlin/kairo/optional/OptionalModule.kt` -- Jackson module for both types
- `src/main/kotlin/kairo/optional/OptionalBase.kt` -- shared base class with `isSpecified` and `getOrThrow()`

## Patterns and conventions
- `Optional<T>`: Missing (field absent) vs Null (field explicitly null) vs Value (field present with value)
- `Required<T>`: Missing (field absent) vs Value (field present with non-null value)
- Use `ifSpecified {}` to conditionally act on present values
- Use `transform {}` to map the inner value while preserving Missing/Null states

## Foot-guns
- You MUST register `OptionalModule` with your Jackson ObjectMapper or KairoJson instance
- You MUST annotate Optional/Required fields with `@JsonInclude(JsonInclude.Include.NON_ABSENT)` or Missing values will serialize as null instead of being omitted
- `getOrThrow()` on `Missing` throws `IllegalStateException`

## Related modules
- **kairo-serialization** -- provides `KairoJson` where `OptionalModule` should be registered
