package kairo.admin

/**
 * Configuration for OAuth 2.0 Authorization Code flow.
 * When provided to [AdminDashboardConfig], the admin login page will redirect
 * to the OAuth provider instead of showing a manual token input form.
 */
public data class AdminOAuthConfig(
  /** Full authorize endpoint URL, e.g. "https://tenant.us.auth0.com/authorize". */
  val authorizeUrl: String,
  /** Full token endpoint URL, e.g. "https://tenant.us.auth0.com/oauth/token". */
  val tokenUrl: String,
  /** OAuth client ID for this application. */
  val clientId: String,
  /** OAuth client secret used to exchange the authorization code for tokens. */
  val clientSecret: String,
  /** OAuth scopes to request. */
  val scopes: List<String> = listOf("openid", "profile", "email"),
  /** API audience (required by Auth0 to return a JWT access token). */
  val audience: String? = null,
  /** Display name shown on the login button, e.g. "Auth0" or "Google". */
  val providerName: String = "OAuth",
  /** Optional logout URL to redirect to after clearing the session cookie. */
  val logoutUrl: String? = null,
)
