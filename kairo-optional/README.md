# Optionals

Differentiate between missing and null values.

[RFC 7396 (JSON Merge Patch)](https://datatracker.ietf.org/doc/html/rfc7396)
specifies:

- Missing properties in JSON PATCH requests
  should be interpreted as "preserve the existing value".
- `null` properties in JSON PATCH requests
  should be interpreted as "remove this property".

In order to implement this, we must have some way to differentiate between missing and null properties.
Traditional serialization libraries like `kotlinx.serialization` and Jackson don't provide this out of the box.
Kairo provides `Optional<T>`.

```kotlin
json.deserialize<Optional>("{}")
// => Optional.Missing

json.deserialize<Optional>("""{"value":null}""")
// => Optional.Null

json.deserialize<Optional>("""{"value":"some value"}""")
// => Optional.Value("some value")
```

## Installation

Install `kairo-optional`.

```kotlin
// build.gradle.kts

dependencies {
  implementation("com.highbeam.kairo:kairo-optional")
}
```

## Usage

There are two important points to note when using `Optional<T>`.

First, you must add `OptionalModule` to your `KairoJson` instance.

```kotlin
val json: KairoJson =
  KairoJson {
    addModule(OptionalModule())
  }
```

Second, you must add the `@JsonInclude(JsonInclude.Include.NON_ABSENT)` annotation
to the class or property.
Without this, `Optional.Missing` will serialize as `null` instead of being omitted.

```kotlin
@JsonInclude(JsonInclude.Include.NON_ABSENT)
data class Update(
  val value: Optional<String> = Optional.Missing,
)
```

### Working with Optional values

Use `ifSpecified` to act only when a value is present (not `Missing`).

```kotlin
update.name.ifSpecified { name ->
  // name is String? here (null if Optional.Null).
  entity.name = name
}
```

Use `transform` to map the inner value while preserving the `Missing` state.

```kotlin
val upperName: Optional<String> = update.name.transform { it?.uppercase() }
```

Use `fromNullable` to convert a nullable value into an `Optional`.

```kotlin
Optional.fromNullable("hello") // => Optional.Value("hello")
Optional.fromNullable(null)    // => Optional.Null
```

### `Required<T>`

`Required<T>` is similar to `Optional<T>` but does not allow `null` values.
It has two states: `Missing` and `Value`.
Use `Required<T>` when a field can be omitted but should never be `null`.

```kotlin
@JsonInclude(JsonInclude.Include.NON_ABSENT)
data class Update(
  val name: Required<String> = Required.Missing,
)
```
