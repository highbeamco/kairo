# kairo-logging

SLF4J-standardized logging exposing Ohad Shai's kotlin-logging interface for a clean, Kotlin-first API. Choose your own logging backend (Log4j, Logback, etc.).

## Key files
- Re-exports `io.github.oshai:kotlin-logging` so consumers get the logging API transitively

## Patterns and conventions
- Create loggers with `private val logger = KotlinLogging.logger {}`
- Use lambda-based logging: `logger.info { "Message with $interpolation" }` (avoids string allocation when level is disabled)
- The logging backend (e.g. Log4j) is a separate runtime dependency; this module only provides the API

## Foot-guns
- If no SLF4J binding is on the classpath at runtime, logging calls will silently no-op
