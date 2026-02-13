package kairo.rest.exception

import io.ktor.http.HttpStatusCode
import kairo.exception.LogicalFailure

/** Thrown when a JWT has expired. Maps to HTTP 401. */
public class ExpiredJwt(
  cause: Throwable? = null,
) : LogicalFailure("Expired JWT", cause) {
  override val type: String = "ExpiredJwt"
  override val status: HttpStatusCode = HttpStatusCode.Unauthorized
}
