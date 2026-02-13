# HOCON

Wrapper around [lightbend/config](https://github.com/lightbend/config)
along with JSON serialization support.

The primary use case is in service of [kairo-config](../kairo-config/README.md).

## Installation

Install `kairo-hocon`.
You don't need to install lightbend/config separately —
it's included by default.

```kotlin
// build.gradle.kts

dependencies {
  implementation("com.highbeam.kairo:kairo-hocon")
}
```

## Usage

The `KairoJson.deserialize(hocon)` extension converts an HOCON `Config` object
to a typed data class.
Internally, the HOCON tree is rendered as JSON and then deserialized by Jackson.

```kotlin
data class Config(
  val rest: RestFeatureConfig,
  val sql: SqlFeatureConfig,
)

val json: KairoJson = KairoJson()

val hocon = ConfigFactory.parseResources("config/$configName.conf").resolve()

json.deserialize<Config>(hocon)
```

You generally won't use this module directly.
[kairo-config](../kairo-config/README.md) uses it under the hood
to load and deserialize HOCON config files.
