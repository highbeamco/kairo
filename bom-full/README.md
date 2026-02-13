# Kairo full BOM

The Kairo full BOM keeps all your Kairo library versions aligned
**and** aligns versions for key external libraries used by Kairo,
such as Ktor, Arrow, Exposed, Jackson, Koin, and others.

Use this BOM when you're building a full Kairo application.
If you're only using one or a few standalone Kairo libraries,
the [standard BOM](../bom/README.md) may be sufficient.

## Installation

```kotlin
// build.gradle.kts

dependencies {
  implementation(platform("com.highbeam.kairo:bom-full:6.0.0"))

  // Now you can omit the version for Kairo libraries and aligned external libraries.
  implementation("com.highbeam.kairo:kairo-rest-feature")
  implementation("com.highbeam.kairo:kairo-sql-feature")
}
```

## What's included

In addition to all Kairo modules, the full BOM aligns versions for:

- [Arrow](https://arrow-kt.io/) — functional programming and coroutines.
- [Exposed](https://www.jetbrains.com/exposed/) — SQL ORM.
- [Jackson](https://github.com/FasterXML/jackson) — JSON serialization.
- [Koin](https://insert-koin.io/) — dependency injection.
- [Ktor](https://ktor.io/) — HTTP client and server.
- [kotlinx-coroutines](https://github.com/Kotlin/kotlinx.coroutines) — coroutines.
- [kotlinx-datetime](https://github.com/Kotlin/kotlinx-datetime) — date/time.
- [Log4j2](https://logging.apache.org/log4j/2.x/) — logging backend.
- [SLF4J](https://www.slf4j.org/) — logging API.
- [Testcontainers](https://www.testcontainers.org/) — integration testing.
- And more (Guava, Moneta, Kotest, MockK, R2DBC, etc.).

See the [full BOM build file](./build.gradle.kts) for the complete list.
