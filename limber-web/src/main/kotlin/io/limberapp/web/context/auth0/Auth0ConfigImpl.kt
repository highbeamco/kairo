package io.limberapp.web.context.auth0

@Suppress("ConstructorParameterNaming")
internal data class Auth0ConfigImpl(
    val domain: String,
    val client_id: String,
    val redirect_uri: String,
    val audience: String
)
