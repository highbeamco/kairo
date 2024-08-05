# Kairo

Kairo is an application framework built for Kotlin.

## Project information

### Major dependencies

- Gradle 8.9
- Kotlin 2.0.0
- Java 21
- Ktor 3.0.0-beta-2
- Guice 7.0.0

## Modules

- [kairo-darb](kairo-darb/):
  Home of `DarbEncoder`, which encodes a list of booleans into a Dense-ish Albeit Readable Binary (DARB) string.
- [kairo-dependency-injection](kairo-dependency-injection/):
  Makes Guice available,
  along with some utilities to make its use more idiomatic.
- [kairo-feature](kairo-feature/):
  Features are the primary building block of Kairo applications.
- [kairo-logging](kairo-logging/):
  Logging uses the [kotlin-logging](https://github.com/oshai/kotlin-logging) interface,
  which should be configured to use Apache Log4j2 under the hood.
- [kairo-rest-feature](kairo-rest-feature/):
  The REST Feature adds support for REST endpoints.
  Under the hood, this Feature uses [Ktor](https://ktor.io/).
- [kairo-server](kairo-server/):
  A Server is an application that runs a set of Features.
- [kairo-testing](kairo-testing/):
  A convenient testing library which includes some helpful test dependencies.

## Brand guidelines

- Treat Kairo _Features_ and _Servers_ as proper nouns (the first letter should be capitalized).
