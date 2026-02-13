package kairo.rest

import io.ktor.server.application.Application

/**
 * Implement this interface on Features that define REST endpoints.
 * The [routing] function is called during Ktor server setup to register routes.
 */
public interface HasRouting {
  public fun Application.routing()
}
