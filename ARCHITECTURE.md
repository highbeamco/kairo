# Architecture

This document explains how Kairo applications are structured
and how the key modules fit together.

## High-level overview

```
┌─────────────────────────────────────────────────┐
│  kairo { }                    (kairo-application)│
│  ┌───────────────────────────────────────────┐  │
│  │  Server                      (kairo-server)│  │
│  │  ┌─────────────────────────────────────┐  │  │
│  │  │  Features                           │  │  │
│  │  │                                     │  │  │
│  │  │  ┌──────────┐  ┌──────────────────┐ │  │  │
│  │  │  │Framework │  │ Domain Features  │ │  │  │
│  │  │  │Features  │  │ (your app code)  │ │  │  │
│  │  │  │          │  │                  │ │  │  │
│  │  │  │ REST     │  │ UserFeature      │ │  │  │
│  │  │  │ SQL      │  │ BillingFeature   │ │  │  │
│  │  │  │ DI       │  │ ...              │ │  │  │
│  │  │  │ ...      │  │                  │ │  │  │
│  │  │  └──────────┘  └──────────────────┘ │  │  │
│  │  └─────────────────────────────────────┘  │  │
│  └───────────────────────────────────────────┘  │
└─────────────────────────────────────────────────┘
```

A Kairo application is composed of:

1. A **`kairo {}` block** that manages the JVM lifecycle.
2. A **Server** that holds and orchestrates Features.
3. **Features** — both framework-provided and domain-specific — that encapsulate functionality.

## Features

Features are the core building block.
Each Feature defines an encapsulated piece of functionality with clear boundaries.

There are two types:

- **Framework Features** provide infrastructure:
  `RestFeature`, `SqlFeature`, `DependencyInjectionFeature`, `HealthCheckFeature`,
  `ClientFeature`, `SlackFeature`, `MailersendFeature`, `StytchFeature`.
- **Domain Features** represent your application concerns:
  users, billing, notifications, or whatever your app needs.

A Domain Feature typically looks like this:

```kotlin
class UserFeature(
  private val koin: Koin,
) : Feature(), HasKoinModules, HasRouting {
  override val name: String = "User"

  override val koinModules: List<Module> = listOf(module)

  private val userHandler: UserHandler get() = koin.get()

  override fun Application.routing() {
    with(userHandler) { routing() }
  }
}
```

Features compose together through interfaces:
- `HasKoinModules` — the Feature provides dependency injection bindings.
- `HasRouting` — the Feature registers REST routes.

See [kairo-feature](./kairo-feature/README.md) for full documentation.

## Servers

A Server is a named container of Features.
It manages the startup and shutdown lifecycle.

```kotlin
val server = Server(
  name = "My Application",
  features = listOf(
    DependencyInjectionFeature(koinApplication),
    HealthCheckFeature(healthChecks),
    RestFeature(config.rest),
    SqlFeature(config.sql, configureDatabase = { explicitDialect = PostgreSQLDialect() }),
    UserFeature(koinApplication.koin),
    BillingFeature(koinApplication.koin),
  ),
)
```

Servers can run as applications (via `kairo-application`)
or be used in integration tests (via `kairo-integration-testing`).

See [kairo-server](./kairo-server/README.md) for full documentation.

## Lifecycle

Features hook into the Server lifecycle through lifecycle handlers with priorities.

**Startup** runs handlers grouped by priority (lower numbers first):

| Priority | Feature | What happens |
|----------|---------|--------------|
| -100,000,000 | DependencyInjection | Scans all Features for `HasKoinModules`, registers Koin bindings. |
| 0 (default) | Domain Features, SQL, etc. | Initialize resources, connect to databases. |
| 100,000,000 | REST | Scans all Features for `HasRouting`, starts the Ktor HTTP server. |
| 200,000,000 | HealthCheck | Registers health check endpoints (after REST is running). |

**Shutdown** runs in reverse priority order.
Features at the same priority level run in parallel,
so lifecycle handlers must be thread/coroutine-safe.

If a Feature fails during startup, the Server attempts to roll back
by calling `stop()` on all Features that already started.

See [kairo-feature](./kairo-feature/README.md) for lifecycle details.

## Dependency injection

Kairo uses [Koin](https://insert-koin.io/) for dependency injection.

During startup, `DependencyInjectionFeature` scans all Features.
Any Feature implementing `HasKoinModules` has its Koin modules registered.
After this, any class can have its dependencies injected via constructor parameters.

The recommended approach uses Koin's KSP annotation processor:

```kotlin
@Single
class UserService(
  private val userStore: UserStore,  // Injected automatically.
)
```

See [kairo-dependency-injection](./kairo-dependency-injection/README.md) for full documentation.

## REST

Kairo's REST layer is built on [Ktor](https://ktor.io/).
It provides a type-safe routing DSL on top of Ktor's standard capabilities.

### Endpoint definition

Endpoints are defined as data classes with annotations:

```kotlin
@Rest("GET", "/users/:userId")
@Rest.Accept("application/json")
data class Get(
  @PathParam val userId: UserId,
) : RestEndpoint<Unit, UserRep>()
```

### Request lifecycle

1. HTTP request arrives at the Ktor/Netty server.
2. Ktor plugins process the request (logging, CORS, auth, headers).
3. The route is matched by path, method, content-type, and accept headers.
4. Path/query parameters are extracted, request body is deserialized.
5. Auth checks run (if configured).
6. The handler executes your business logic.
7. The response is serialized and sent back.

### Exception handling

[Logical failures](./kairo-exception/README.md) are automatically mapped to semantic HTTP responses.
Deserialization errors produce structured 400 responses.

See [kairo-rest](./kairo-rest/README.md) for full documentation.

## Configuration

Kairo uses [HOCON](https://github.com/lightbend/config) for configuration.

Key capabilities:
- **Multi-environment support:** Base config with per-environment overrides (dev/staging/prod).
- **Environment variable substitution:** `${?MY_VAR}` syntax.
- **Config resolvers:** Pull values from external sources like Google Secret Manager.

```hocon
# production.conf
include "common.conf"
sql.connectionFactory {
  url = ${?POSTGRES_URL}
  password = "gcp::projects/012345678900/secrets/db-password/versions/1"
}
```

See [kairo-config](./kairo-config/README.md) for full documentation.

## Standalone vs. application libraries

Kairo's libraries fall into two categories:

- **Standalone libraries** can be used in any Kotlin project.
  They have no dependency on the Feature/Server model.
  Examples: `kairo-id`, `kairo-money`, `kairo-serialization`, `kairo-validation`.

- **Application libraries** are designed for building full Kairo applications.
  They use or integrate with the Feature/Server model.
  Examples: `kairo-rest`, `kairo-sql`, `kairo-dependency-injection`.

Both types are versioned together and managed through Kairo's BOM.

## Module dependency map

Core application modules depend on each other as follows:

```
kairo-application
  └── kairo-server
        └── kairo-feature

kairo-rest:feature
  ├── kairo-rest:endpoint
  ├── kairo-exception
  ├── kairo-ktor (internal)
  ├── kairo-logging
  ├── kairo-reflect
  └── kairo-serialization

kairo-dependency-injection:feature
  └── kairo-feature

kairo-sql:feature
  ├── kairo-logging
  └── kairo-config

kairo-config
  ├── kairo-hocon
  └── kairo-serialization
```

Standalone libraries generally have minimal dependencies on each other.
