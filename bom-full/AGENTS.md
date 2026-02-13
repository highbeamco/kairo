# bom-full (Full Bill of Materials)

Extends `bom` with version alignment for all external dependencies used by Kairo. Ensures consistent versions of Arrow, Exposed, Jackson, Koin, Ktor, Log4j, and many more.

## Key files
- `build.gradle.kts` -- lists all managed external dependency versions and their BOMs/constraints

## Managed external dependencies
- Arrow, Coroutines, Exposed, GCP libraries, Guava
- Jackson, HOCON (config), Koin, Ktor, Log4j, SLF4J
- MailerSend, Slack, Stytch, Postgres (JDBC + R2DBC), Testcontainers
- Kotest, MockK, kotlinx-datetime, Moneta

## Patterns and conventions
- Consumers use `implementation(platform("com.highbeam.kairo:bom-full:VERSION"))` to align both Kairo and external versions
- When updating an external dependency, update its version here

## Related modules
- `bom` -- included via `api(platform(project(":bom")))` for Kairo module alignment
