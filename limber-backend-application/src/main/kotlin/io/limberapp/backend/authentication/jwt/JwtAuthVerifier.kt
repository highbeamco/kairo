package io.limberapp.backend.authentication.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.piperframework.config.authentication.AuthenticationConfig
import com.piperframework.config.authentication.AuthenticationMechanism
import com.piperframework.ktorAuth.PiperAuthVerifier
import io.limberapp.backend.authorization.principal.Claims
import io.limberapp.backend.authorization.principal.Jwt
import io.limberapp.backend.authorization.principal.JwtOrg
import io.limberapp.backend.authorization.principal.JwtRole
import io.limberapp.backend.authorization.principal.JwtUser
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.parse
import kotlinx.serialization.parseList
import org.slf4j.LoggerFactory

class JwtAuthVerifier(authenticationConfig: AuthenticationConfig) : PiperAuthVerifier<Jwt> {

    private val logger = LoggerFactory.getLogger(JwtAuthVerifier::class.java)

    private val json = Json(JsonConfiguration.Stable) // TODO: Don't duplicat this config

    private val providers = authenticationConfig.mechanisms.associate { mechanism ->
        val provider = when (mechanism) {
            is AuthenticationMechanism.Jwk ->
                UrlJwtVerifierProvider(mechanism.domain)
            is AuthenticationMechanism.Jwt ->
                StaticJwtVerifierProvider(JWT.require(Algorithm.HMAC256(mechanism.secret)).build())
            is AuthenticationMechanism.UnsignedJwt ->
                StaticJwtVerifierProvider(JWT.require(Algorithm.none()).build())
        }
        return@associate Pair(mechanism.issuer, provider)
    }

    override fun verify(blob: String): Jwt? {
        val decodedJwt = try {
            getVerifier(blob)?.verify(blob)
        } catch (e: JWTVerificationException) {
            logger.warn("JWT verification exception", e)
            null
        } ?: return null
        return Jwt(
            org = decodedJwt.getClaim(Claims.org).asString()?.let { json.parse<JwtOrg>(it) },
            roles = requireNotNull(decodedJwt.getClaim(Claims.roles).asString()).let { json.parseList<JwtRole>(it) },
            user = requireNotNull(decodedJwt.getClaim(Claims.user).asString()).let { json.parse<JwtUser>(it) }
        )
    }

    private fun getVerifier(blob: String): JWTVerifier? {
        val jwt = JWT.decode(blob)
        val provider = providers[jwt.issuer] ?: return null
        return provider[jwt.keyId]
    }

    companion object {
        const val scheme = "Bearer"
    }
}
