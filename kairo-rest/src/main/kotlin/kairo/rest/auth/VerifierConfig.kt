package kairo.rest.auth

import kotlin.time.Duration

/** Configuration for JWT verification using RSA256 with a JWKS endpoint. */
public data class VerifierConfig(
  /** URL of the JWKS endpoint (for example "https://example.com/.well-known/jwks.json"). */
  val jwkUrl: String,
  /** Expected JWT issuer claim. */
  val issuer: String,
  /** Expected JWT audience claim. Null to skip audience validation. */
  val audience: String? = null,
  /** Additional claims to require in the JWT. */
  val claims: Map<String, String> = emptyMap(),
  /** Clock skew tolerance for token expiration checks. */
  val leeway: Duration,
)
