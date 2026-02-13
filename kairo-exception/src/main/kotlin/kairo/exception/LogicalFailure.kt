package kairo.exception

import io.ktor.http.HttpStatusCode

/**
 * "Logical failures" describe situations not deemed successful in your domain,
 * but still within the realms of that domain.
 * For example, a user record not being found is a logical failure, not a real exception.
 * Whereas a network timeout or stack overflow is a "real exception".
 *
 * Roughly conforms to RFC 9457, but not strictly.
 */
public abstract class LogicalFailure(
  message: String,
  cause: Throwable? = null,
) : Exception(message, cause) {
  /** Machine-readable discriminator for this failure type (for example "UserNotFound"). */
  public abstract val type: String

  /** The HTTP status code this failure maps to. */
  public abstract val status: HttpStatusCode

  /** Additional human-readable context that varies per occurrence. */
  public open val detail: String? = null

  /** Lazily-computed JSON representation of this failure, roughly conforming to RFC 9457. */
  public open val json: Map<String, Any?> by lazy {
    buildMap {
      put("type", type)
      put("status", status)
      put("message", message)
      put("detail", detail)
      buildJson()
    }
  }

  /** Override to add domain-specific fields to the [json] representation. */
  protected open fun MutableMap<String, Any?>.buildJson(): Unit =
    Unit
}
