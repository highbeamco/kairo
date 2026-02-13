package kairo.rest.auth

import io.ktor.server.auth.AuthenticationConfig

/** Configures authentication for all REST endpoints in the Server. */
public abstract class AuthConfig {
  /** Installs Ktor authentication providers (for example bearer token extraction). */
  public abstract fun AuthenticationConfig.configure()

  /** Called when an endpoint does not define its own auth block. Override to set a default policy. */
  public open suspend fun AuthReceiver<*>.default() {
    error("This endpoint must implement auth.")
  }

  /** Called as a last resort after all other auth blocks fail. Override to customize error behavior. */
  public open suspend fun AuthReceiver<*>.fallback() {
    error("No auth fallback.")
  }
}
