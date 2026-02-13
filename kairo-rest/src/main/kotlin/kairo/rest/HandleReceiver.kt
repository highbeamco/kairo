package kairo.rest

import io.ktor.server.routing.RoutingCall

/**
 * Receiver context for REST endpoint handlers.
 * Provides access to the raw Ktor [call] and the deserialized [endpoint].
 */
@Suppress("UseDataClass")
public class HandleReceiver<E : RestEndpoint<*, *>> internal constructor(
  /** The underlying Ktor routing call. Use to access headers, cookies, or other request details. */
  public val call: RoutingCall,
  /** The deserialized endpoint instance, populated from path params, query params, and the request body. */
  public val endpoint: E,
)
