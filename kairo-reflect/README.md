# Kairo Reflect

`kairo-reflect` extends Kotlin's reflection library.

### `KairoType`

JVM/Kotlin reflection is powerful,
but the standard APIs are fragmented
across `Class<T>`, `KClass<T>`, `Type`, and `KType`.
`kairo-reflect` unifies these into a single, runtime-rich abstraction: `KairoType<T>`.

- **Full generic fidelity:**
  Preserves type arguments at runtime (`List<String>`, not just `List<*>`).
- **Unified access:**
  Seamlessly move between Java and Kotlin reflection models.
- **Runtime inference:**
  Extract generic parameter types without boilerplate.
- **Safer metaprogramming:**
  Fewer lossy conversions, fewer reflection edge cases.

This makes dynamic frameworks (serialization, dependency injection, query builders)
more **reliable and maintainable**.

## Installation

Install `kairo-reflect`.
You don't need to install Kotlin's reflection library separately —
it's included by default.

```kotlin
// build.gradle.kts

dependencies {
  implementation("com.highbeam.kairo:kairo-reflect")
}
```

## Usage

### `kairoType`

Capture a rich `KairoType<T>` that includes context from **all major reflection models**.

```kotlin
val type: KairoType<T> = kairoType<MyClass<String>>()

val javaClass: Class<T> = type.javaClass
val kotlinClass: KClass<T> = type.kotlinClass
val javaType: Type = type.javaType
val kotlinType: KType = type.kotlinType
```

### `KairoType.from()`

Infer a generic type parameter at runtime with **no manual plumbing**.
This is useful when you need to know a subclass's type argument
inside a base class.

```kotlin
abstract class MyClass<T> {
  val type: KairoType<T> = KairoType.from(MyClass::class, 0, this::class)
}
```

The arguments are:

- `baseClass`: The class that declares the type parameter.
- `i`: The zero-based index of the type parameter to extract.
- `thisClass`: The concrete subclass (typically `this::class`).

Kairo uses `KairoType.from()` internally in `RestEndpoint`
to resolve the input and output types at runtime for serialization.
