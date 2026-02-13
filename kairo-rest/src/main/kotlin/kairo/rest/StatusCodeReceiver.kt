package kairo.rest

import io.ktor.server.routing.RoutingCall

/**
 * Receiver context for customizing the HTTP response status code.
 * Return a status code to override Ktor's default, or null to keep it.
 */
@Suppress("UseDataClass")
public class StatusCodeReceiver<O : Any> internal constructor(
  /** The underlying Ktor routing call. */
  public val call: RoutingCall,
  /** The response object returned by the handler. */
  public val response: O,
)
