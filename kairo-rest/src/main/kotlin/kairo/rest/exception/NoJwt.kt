package kairo.rest.exception

import io.ktor.http.HttpStatusCode
import kairo.exception.LogicalFailure

/** Thrown when a request requires authentication but no JWT is present. Maps to HTTP 401. */
public class NoJwt(
  cause: Throwable? = null,
) : LogicalFailure("No JWT", cause) {
  override val type: String = "NoJwt"
  override val status: HttpStatusCode = HttpStatusCode.Unauthorized
}
