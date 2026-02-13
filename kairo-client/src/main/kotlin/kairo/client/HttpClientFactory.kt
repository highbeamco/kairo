package kairo.client

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.java.Java
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import kairo.ktor.kairoConversion
import kairo.serialization.KairoJson
import kotlin.time.Duration

/** Factory for creating pre-configured Ktor HTTP clients with JSON support and timeouts. */
public object HttpClientFactory {
  /**
   * Creates a Ktor HTTP client using the Java engine.
   *
   * @param timeout Request timeout applied to all requests.
   * @param json KairoJson instance for content negotiation.
   * @param block Additional client configuration (default request settings, plugins, etc.).
   */
  public fun create(
    timeout: Duration,
    json: KairoJson,
    block: HttpClientConfig<*>.() -> Unit,
  ): HttpClient =
    HttpClient(Java) {
      expectSuccess = true
      install(ContentNegotiation) {
        kairoConversion(json)
      }
      install(HttpTimeout) {
        requestTimeoutMillis = timeout.inWholeMilliseconds
      }
      block()
    }
}
