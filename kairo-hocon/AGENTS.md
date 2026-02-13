# kairo-hocon

Wrapper around lightbend/config (HOCON) with JSON serialization support via KairoJson.

## Key files
- `src/main/kotlin/kairo/hocon/KairoJson.kt` -- `KairoJson.deserialize(Config)` extension that renders HOCON as JSON before deserializing

## Patterns and conventions
- HOCON `Config` objects are rendered to JSON internally, then deserialized via `KairoJson`
- The reified inline function captures full generic type info via `kairoType<T>()`

## Related modules
- **kairo-serialization** -- `KairoJson` is the Jackson wrapper used for deserialization
- **kairo-reflect** -- `KairoType` preserves generic type info
- **kairo-config** -- primary consumer; uses this to deserialize config files
