# Kairo Client

[Ktor-native](https://ktor.io/docs/client-requests.html)
outgoing HTTP requests from your Kairo application.

## Installation

Install `kairo-client-feature`.
You don't need to install Ktor dependencies separately —
they're included by default.

```kotlin
// build.gradle.kts

dependencies {
  implementation("com.highbeam.kairo:kairo-client-feature")
}
```

## Usage

Create a separate Feature for each external integration.
Each `ClientFeature` manages its own Ktor `HttpClient` lifecycle
and registers it with DI under the given name.

```kotlin
class WeatherFeature : ClientFeature(httpClientName = "weather") {
  override val name: String = "Weather"

  override val timeout: Duration = 5.seconds

  override fun HttpClientConfig<*>.configure() {
    defaultRequest {
      url("https://api.weather.gov")
      userAgent("kairo (jeff@example.com)")
    }
  }
}
```

The `timeout` property sets the request timeout for the client.
Use the `configure()` block to set default request parameters,
install Ktor plugins, or add custom headers.

### Making requests

Inject the corresponding Ktor `HttpClient` by name
and use it to make requests.

```kotlin
@Named("weather") val weatherClient: HttpClient

val response = weatherClient.request {
  method = HttpMethod.Get
  url("/gridpoints/LWX/96,70/forecast")
}
return response.body()
```

### Multiple clients

Since each `ClientFeature` creates a separate `HttpClient`,
you can have as many as you need.
Each is registered under its `httpClientName` in DI
and can be injected independently.

### Advanced usage

Refer to the [Ktor client documentation](https://ktor.io/docs/client-requests.html)
for more advanced usage.
