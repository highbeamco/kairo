# kairo-serialization

Jackson wrapper (`KairoJson`) with strict, opinionated defaults. Enforces Kotlin nullability, fails on unknown properties by default, and preserves full generic type information via kairo-reflect.

## Key files
- `src/main/kotlin/kairo/serialization/KairoJson.kt` -- main entry point; `KairoJson { }` builder, `serialize()`, `deserialize()`, and the exhaustive `kairo()` builder configuring all Jackson features
- `src/main/kotlin/kairo/serialization/BigDecimalConfig.kt` -- `BigDecimalFormat` enum (Double vs String)
- `src/main/kotlin/kairo/serialization/BigIntegerConfig.kt` -- `BigIntegerFormat` enum (Long vs String)

## Patterns and conventions
- Create instances via `KairoJson { }` builder; configure with `addModule()`, `configure()`, `allowUnknown`, `pretty`
- All serialization/deserialization goes through `KairoType` to avoid Jackson type erasure issues
- `FAIL_ON_UNKNOWN_PROPERTIES` is true by default; set `allowUnknown = true` to disable
- `checkResult()` enforces Kotlin nullability after deserialization
- Accessing the raw `JsonMapper` requires `@OptIn(KairoJson.RawJsonMapper::class)`

## Foot-guns
- The `kairo()` function explicitly configures hundreds of Jackson features; adding features elsewhere may be overridden
- `FAIL_ON_TRAILING_TOKENS` is disabled due to a Jackson bug (see source comment)
- `ALLOW_COERCION_OF_SCALARS` is false by default but enabled in kairo-config for env var handling
- Registering duplicate modules is explicitly disallowed (`IGNORE_DUPLICATE_MODULE_REGISTRATIONS = false`)

## Related modules
- **kairo-reflect** -- provides `KairoType<T>` for type-safe serialization
- **kairo-optional** -- `OptionalModule` must be registered separately
- **kairo-money** -- `MoneyModule` must be registered separately
- **kairo-hocon** -- uses `KairoJson` for HOCON deserialization
