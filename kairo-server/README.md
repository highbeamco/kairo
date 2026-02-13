# Kairo Server

Servers are composed of [Features](../kairo-feature/README.md),
and can be run as an application or as part of integration tests.

A Server manages the full lifecycle of its Features:
starting them in priority order, running them together, and stopping them gracefully on shutdown.

## Installation

Install `kairo-server`.
But if you've already installed `kairo-application`,
you don't need `kairo-server` too.

```kotlin
// build.gradle.kts

dependencies {
  implementation("com.highbeam.kairo:kairo-server")
}
```

## Usage

All you need are your [Features](../kairo-feature/README.md) and a name.

```kotlin
val features = listOf(
  DependencyInjectionFeature(koinApplication),
  HealthCheckFeature(healthChecks),
  RestFeature(config.rest),
  SqlFeature(config.sql, configureDatabase = { explicitDialect = PostgreSQLDialect() }),
  UserFeature(koinApplication.koin),
)

val server = Server(
  name = "My Application",
  features = features,
)
```

You can run the Server as an application using [kairo-application](../kairo-application/README.md),
or use it in [integration tests](../kairo-integration-testing/README.md).

### Lifecycle

On **startup**, the Server groups all Feature lifecycle handlers by priority
and starts each group sequentially (lowest priority first).
Features within the same priority group start in parallel.

On **shutdown**, the Server stops Features in reverse priority order.
Shutdown uses `supervisorScope`, meaning all Features get a chance to clean up
even if one fails to stop.

If a Feature fails during startup,
the Server rolls back by stopping all Features that already started.

See [kairo-feature](../kairo-feature/README.md) for details on lifecycle handlers and priorities.

### Server state

A Server progresses through these states: `Default` → `Starting` → `Running` → `Stopping` → `Default`.
The `state` property is volatile and can be read without synchronization.
