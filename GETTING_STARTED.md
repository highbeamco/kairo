# Getting started

This guide walks you through using Kairo in your project.

**[Documentation](https://kairo.airborne.software)** · **[API Reference](https://kairo.airborne.software/api/)**

## Prerequisites

- **JDK 21** (Kairo targets the Corretto distribution, but any JDK 21 will work).
- **Gradle** (Kotlin DSL). The Gradle wrapper is recommended.

## Repository setup

Kairo is hosted on Google Artifact Registry.
Add the Google Artifact Registry plugin and connect to the Highbeam repository.

```kotlin
// build.gradle.kts

plugins {
  id("com.google.cloud.artifactregistry.gradle-plugin")
}

repositories {
  maven {
    url = uri("artifactregistry://us-central1-maven.pkg.dev/highbeam-kairo/maven")
  }
}
```

## Using standalone libraries

Kairo's [standalone libraries](./README.md#standalone-libraries)
can be used independently within any Kotlin project.
You do not need the Feature/Server model to use them.

We recommend using [Kairo's BOM](./bom/README.md) to keep versions aligned.

```kotlin
// build.gradle.kts

dependencies {
  implementation(platform("com.highbeam.kairo:bom:6.0.0"))
  implementation("com.highbeam.kairo:kairo-id")
  implementation("com.highbeam.kairo:kairo-money")
}
```

That's it. Refer to each library's README for usage details.

## Building a full Kairo application

If you want to build your entire backend with Kairo,
you'll use the Feature/Server model.
This section walks through the key steps.

For an overview of how Features, Servers, and the lifecycle model work,
see [ARCHITECTURE.md](./ARCHITECTURE.md).

### 1. Set up Gradle

Use [Kairo's full BOM](./bom-full/README.md),
which aligns both Kairo and external library versions.

```kotlin
// build.gradle.kts

plugins {
  id("com.google.cloud.artifactregistry.gradle-plugin")
  id("com.google.devtools.ksp")  // For Koin annotation processing.
}

repositories {
  maven {
    url = uri("artifactregistry://us-central1-maven.pkg.dev/highbeam-kairo/maven")
  }
}

dependencies {
  implementation(platform("com.highbeam.kairo:bom-full:6.0.0"))

  // Core.
  implementation("com.highbeam.kairo:kairo-application")
  implementation("com.highbeam.kairo:kairo-config")

  // Framework Features.
  implementation("com.highbeam.kairo:kairo-dependency-injection-feature")
  implementation("com.highbeam.kairo:kairo-health-check-feature")
  implementation("com.highbeam.kairo:kairo-rest-feature")
  implementation("com.highbeam.kairo:kairo-sql-feature")
  ksp("io.insert-koin:koin-ksp-compiler")

  // Database driver.
  runtimeOnly("org.postgresql:r2dbc-postgresql")
}
```

### 2. Define your configuration

Create a config data class and HOCON config files.

```kotlin
// src/main/kotlin/myApp/Config.kt

data class Config(
  val rest: RestFeatureConfig,
  val sql: SqlFeatureConfig,
)
```

```hocon
# src/main/resources/common.conf

rest {
  connector.port = 8080
  plugins.defaultHeaders.serverName = "My Application"
}
```

```hocon
# src/main/resources/development.conf

include "common.conf"

rest.plugins.callLogging.useColors = true

sql.connectionFactory {
  url = "r2dbc:postgresql://localhost/my_app"
  username = ${?POSTGRES_USERNAME}
  password = ${?POSTGRES_PASSWORD}
  ssl = false
}
```

See [kairo-config](./kairo-config/README.md) for more on HOCON configuration.

### 3. Create a Domain Feature

Each Domain Feature represents a concern in your application.

Start by defining your model and REST API:

```kotlin
data class UserRep(
  val id: UserId,
  val emailAddress: String,
) {
  data class Creator(
    val emailAddress: String,
  )
}

object UserApi {
  @Rest("GET", "/users/:userId")
  @Rest.Accept("application/json")
  data class Get(
    @PathParam val userId: UserId,
  ) : RestEndpoint<Unit, UserRep>()

  @Rest("POST", "/users")
  @Rest.ContentType("application/json")
  @Rest.Accept("application/json")
  data class Create(
    override val body: UserRep.Creator,
  ) : RestEndpoint<UserRep.Creator, UserRep>()
}
```

Create your service and handler with Koin annotations:

```kotlin
@Single
class UserService(
  private val userStore: UserStore,  // Injected automatically.
) {
  suspend fun get(id: UserId): UserModel? =
    userStore.get(id)

  suspend fun create(creator: UserModel.Creator): UserModel =
    userStore.create(creator)
}

@Single
class UserHandler(
  private val userService: UserService,
) : HasRouting {
  override fun Application.routing() {
    routing {
      route(UserApi.Get::class) {
        handle {
          val user = userService.get(endpoint.userId)
            ?: throw UserNotFound(endpoint.userId)
          UserRep(user.id, user.emailAddress)
        }
      }
      route(UserApi.Create::class) {
        handle {
          val user = userService.create(
            UserModel.Creator(emailAddress = endpoint.body.emailAddress),
          )
          UserRep(user.id, user.emailAddress)
        }
      }
    }
  }
}
```

Wire it all up in a Feature:

```kotlin
@org.koin.core.annotation.Module
@org.koin.core.annotation.ComponentScan
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

### 4. Wire Features into a Server and run

```kotlin
fun main() {
  kairo {
    val config = loadConfig<Config>()
    val koinApplication = koinApplication()

    val healthChecks = mapOf(
      "sql" to HealthCheck { SqlFeature.healthCheck(koinApplication.koin.get()) },
    )

    val features = listOf(
      // Framework Features.
      DependencyInjectionFeature(koinApplication),
      HealthCheckFeature(healthChecks),
      RestFeature(config = config.rest, authConfig = null),
      SqlFeature(
        config = config.sql,
        configureDatabase = { explicitDialect = PostgreSQLDialect() },
      ),

      // Domain Features.
      UserFeature(koinApplication.koin),
    )

    val server = Server(
      name = "My Application",
      features = features,
    )
    server.startAndWait(
      release = {
        server.stop()
        LogManager.shutdown()
      },
    )
  }
}
```

Set the `CONFIG` environment variable (or pass a config name) and run your application.
The Server will start all Features in priority order and listen for HTTP requests.

## Testing

Kairo recommends black-box integration testing at the service layer.
See [kairo-integration-testing](./kairo-integration-testing/README.md) for details.

## Next steps

- Read the [Architecture overview](./ARCHITECTURE.md) to understand how the pieces fit together.
- Browse individual library READMEs for detailed usage — they are linked from the [root README](./README.md).
- Check the [API Reference](https://kairo.airborne.software/api/) for complete API documentation.
