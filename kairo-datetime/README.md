# Kairo Date/Time

`kairo-datetime` extends [kotlinx-datetime](https://github.com/Kotlin/kotlinx-datetime)
and adds some **convenient helper functions**.

## Installation

Install `kairo-datetime`.
You don't need to install Kotlin's datetime library separately —
it's included by default.

```kotlin
// build.gradle.kts

dependencies {
  implementation("com.highbeam.kairo:kairo-datetime")
}
```

## Usage

This library re-exports [kotlinx-datetime](https://github.com/Kotlin/kotlinx-datetime) types
and adds convenience extensions.

### `Instant.epoch`

A shorthand for the Unix epoch (`1970-01-01T00:00:00Z`).
Useful in tests when you need a stable, deterministic timestamp.

```kotlin
Instant.epoch // 1970-01-01T00:00:00Z
```
