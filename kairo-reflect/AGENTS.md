# kairo-reflect

`KairoType<T>` unifies JVM reflection types (`Class`, `KClass`, `Type`, `KType`) into a single wrapper that preserves full generic type information at runtime. Used internally by kairo-serialization for type-safe Jackson operations.

## Key files
- `src/main/kotlin/kairo/reflect/KairoType.kt` -- `KairoType<T>` data class wrapping `KType`, with `kairoType<T>()` inline factory function and `KairoType.from()` for runtime inference

## Patterns and conventions
- Use `kairoType<MyType>()` (inline reified) to capture generic type info at call sites
- Use `KairoType.from(baseClass, i, thisClass)` to infer type parameters from within abstract generic classes at runtime
- `KairoType` is a data class, so equality and hashing are based on the underlying `KType`

## Foot-guns
- `kairoType<T>()` must be called at a site where `T` is reified; it cannot be called with erased type parameters
- `KairoType.from()` relies on Kotlin reflection (`KClass.allSupertypes`); ensure `kotlin-reflect` is on the classpath

## Related modules
- **kairo-serialization** -- primary consumer; uses `KairoType` to create Jackson `TypeReference` instances with full generic info
