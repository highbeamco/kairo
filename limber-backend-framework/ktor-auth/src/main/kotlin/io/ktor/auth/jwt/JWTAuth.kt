/*
 * Copyright 2014-2019 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package io.ktor.auth.jwt

import com.auth0.jwk.Jwk
import com.auth0.jwk.JwkException
import com.auth0.jwk.JwkProvider
import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTDecodeException
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.impl.JWTParser
import com.auth0.jwt.interfaces.DecodedJWT
import com.auth0.jwt.interfaces.Payload
import com.auth0.jwt.interfaces.Verification
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.Authentication
import io.ktor.auth.AuthenticationContext
import io.ktor.auth.AuthenticationFailedCause
import io.ktor.auth.AuthenticationFunction
import io.ktor.auth.AuthenticationPipeline
import io.ktor.auth.AuthenticationProvider
import io.ktor.auth.Credential
import io.ktor.auth.Principal
import io.ktor.auth.UnauthorizedResponse
import io.ktor.auth.parseAuthorizationHeader
import io.ktor.http.auth.HttpAuthHeader
import io.ktor.request.ApplicationRequest
import io.ktor.response.respond
import io.ktor.util.pipeline.PipelineContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.security.interfaces.ECPublicKey
import java.security.interfaces.RSAPublicKey
import java.util.Base64

private val JWTAuthKey: Any = "JWTAuth"

private val JWTLogger: Logger = LoggerFactory.getLogger("io.ktor.auth.jwt")

/**
 * Represents a JWT credential consist of the specified [payload]
 * @param payload JWT
 * @see Payload
 */
class JWTCredential(val payload: Payload) : Credential

/**
 * Represents a JWT principal consist of the specified [payload]
 * @param payload JWT
 * @see Payload
 */
class JWTPrincipal(val payload: Payload) : Principal

/**
 * JWT verifier configuration function. It is applied on the verifier builder.
 */
typealias JWTConfigureFunction = Verification.() -> Unit

/**
 * JWT authentication provider that will be registered with the specified [name]
 */
class JWTAuthenticationProvider internal constructor(config: Configuration) :
    AuthenticationProvider(config) {

    internal val realm: String = config.realm
    internal val schemes: JWTAuthSchemes = config.schemes
    internal val authHeader: (ApplicationCall) -> HttpAuthHeader? = config.authHeader
    internal val verifier: ((HttpAuthHeader) -> JWTVerifier?) = config.verifier
    internal val authenticationFunction = config.authenticationFunction
    internal val challengeFunction: JWTAuthChallengeFunction = config.challenge

    /**
     * JWT auth provider configuration
     */
    class Configuration internal constructor(name: String?) :
        AuthenticationProvider.Configuration(name) {
        internal var authenticationFunction: AuthenticationFunction<JWTCredential> = { null }

        internal var schemes = JWTAuthSchemes("Bearer")

        internal var authHeader: (ApplicationCall) -> HttpAuthHeader? =
            { call -> call.request.parseAuthorizationHeaderOrNull() }

        internal var verifier: ((HttpAuthHeader) -> JWTVerifier?) = { null }

        internal var challenge: JWTAuthChallengeFunction = { scheme, realm ->
            call.respond(
                UnauthorizedResponse(
                    HttpAuthHeader.Parameterized(
                        scheme,
                        mapOf(HttpAuthHeader.Parameters.Realm to realm)
                    )
                )
            )
        }

        /**
         * JWT realm name that will be used during auth challenge
         */
        var realm: String = "Ktor Server"

        /**
         * Http auth header retrieval function. By default it does parse `Authorization` header content.
         */
        fun authHeader(block: (ApplicationCall) -> HttpAuthHeader?) {
            authHeader = block
        }

        /**
         * @param [defaultScheme] default scheme that will be used to challenge the client when no valid auth is provided
         * @param [additionalSchemes] additional schemes that will be accepted when validating the authentication
         */
        fun authSchemes(defaultScheme: String = "Bearer", vararg additionalSchemes: String) {
            schemes = JWTAuthSchemes(defaultScheme, *additionalSchemes)
        }

        /**
         * @param [verifier] verifies token format and signature
         */
        fun verifier(verifier: JWTVerifier) {
            this.verifier = { verifier }
        }

        /**
         * @param [verifier] verifies token format and signature
         */
        fun verifier(verifier: (HttpAuthHeader) -> JWTVerifier?) {
            this.verifier = verifier
        }

        /**
         * @param [jwkProvider] provides the JSON Web Key
         * @param [issuer] the issuer of the JSON Web Token
         * * @param configure function will be applied during [JWTVerifier] construction
         */
        fun verifier(
            jwkProvider: JwkProvider,
            issuer: String,
            configure: JWTConfigureFunction = {}
        ) {
            this.verifier = { token -> getVerifier(jwkProvider, issuer, token, schemes, configure) }
        }

        /**
         * @param [jwkProvider] provides the JSON Web Key
         * @param configure function will be applied during [JWTVerifier] construction
         */
        fun verifier(jwkProvider: JwkProvider, configure: JWTConfigureFunction = {}) {
            this.verifier = { token -> getVerifier(jwkProvider, token, schemes, configure) }
        }

        /**
         * Apply [validate] function to every call with [JWTCredential]
         * @return a principal (usually an instance of [JWTPrincipal]) or `null`
         */
        fun validate(validate: suspend ApplicationCall.(JWTCredential) -> Principal?) {
            authenticationFunction = validate
        }

        /**
         * Specifies what to send back if jwt authentication fails.
         */
        fun challenge(block: JWTAuthChallengeFunction) {
            challenge = block
        }

        internal fun build() = JWTAuthenticationProvider(this)
    }
}

internal class JWTAuthSchemes(val defaultScheme: String, vararg additionalSchemes: String) {
    val schemes = (arrayOf(defaultScheme) + additionalSchemes).toSet()
    val schemesLowerCase = schemes.map { it.toLowerCase() }.toSet()

    operator fun contains(scheme: String): Boolean = scheme.toLowerCase() in schemesLowerCase
}

/**
 * Installs JWT Authentication mechanism
 */
fun Authentication.Configuration.jwt(
    name: String? = null,
    configure: JWTAuthenticationProvider.Configuration.() -> Unit
) {
    val provider = JWTAuthenticationProvider.Configuration(name).apply(configure).build()
    val realm = provider.realm
    val authenticate = provider.authenticationFunction
    val verifier = provider.verifier
    val schemes = provider.schemes
    provider.pipeline.intercept(AuthenticationPipeline.RequestAuthentication) { context ->
        val token = provider.authHeader(call)
        if (token == null) {
            context.bearerChallenge(
                AuthenticationFailedCause.NoCredentials,
                realm,
                schemes,
                provider.challengeFunction
            )
            return@intercept
        }

        try {
            val principal = verifyAndValidate(call, verifier(token), token, schemes, authenticate)
            if (principal != null) {
                context.principal(principal)
            } else {
                context.bearerChallenge(
                    AuthenticationFailedCause.InvalidCredentials,
                    realm,
                    schemes,
                    provider.challengeFunction
                )
            }
        } catch (cause: Throwable) {
            val message = cause.message ?: cause.javaClass.simpleName
            JWTLogger.trace("JWT verification failed: {}", message)
            context.error(JWTAuthKey, AuthenticationFailedCause.Error(message))
        }
    }
    register(provider)
}

/**
 * Specifies what to send back if session authentication fails.
 */
typealias JWTAuthChallengeFunction =
        suspend PipelineContext<*, ApplicationCall>.(defaultScheme: String, realm: String) -> Unit

private fun AuthenticationContext.bearerChallenge(
    cause: AuthenticationFailedCause,
    realm: String,
    schemes: JWTAuthSchemes,
    challengeFunction: JWTAuthChallengeFunction
) = challenge(JWTAuthKey, cause) {
    challengeFunction(this, schemes.defaultScheme, realm)
    if (!it.completed && call.response.status() != null) {
        it.complete()
    }
}

private fun getVerifier(
    jwkProvider: JwkProvider,
    issuer: String?,
    token: HttpAuthHeader,
    schemes: JWTAuthSchemes,
    jwtConfigure: Verification.() -> Unit
): JWTVerifier? {
    val jwk = token.getBlob(schemes)?.let { blob ->
        try {
            jwkProvider.get(JWT.decode(blob).keyId)
        } catch (ex: JwkException) {
            JWTLogger.trace("Failed to get JWK: {}", ex.message)
            null
        } catch (ex: JWTDecodeException) {
            JWTLogger.trace("Illegal JWT: {}", ex.message)
            null
        }
    } ?: return null

    val algorithm = try {
        jwk.makeAlgorithm()
    } catch (cause: Throwable) {
        JWTLogger.trace(
            "Failed to create algorithm {}: {}",
            jwk.algorithm,
            cause.message ?: cause.javaClass.simpleName
        )
        return null
    }

    return when (issuer) {
        null -> JWT.require(algorithm)
        else -> JWT.require(algorithm).withIssuer(issuer)
    }.apply(jwtConfigure).build()
}

private fun getVerifier(
    jwkProvider: JwkProvider,
    token: HttpAuthHeader,
    schemes: JWTAuthSchemes,
    configure: JWTConfigureFunction
): JWTVerifier? {
    return getVerifier(jwkProvider, null, token, schemes, configure)
}

private suspend fun verifyAndValidate(
    call: ApplicationCall,
    jwtVerifier: JWTVerifier?,
    token: HttpAuthHeader,
    schemes: JWTAuthSchemes,
    validate: suspend ApplicationCall.(JWTCredential) -> Principal?
): Principal? {
    val jwt = try {
        token.getBlob(schemes)?.let { jwtVerifier?.verify(it) }
    } catch (ex: JWTVerificationException) {
        JWTLogger.trace("Token verification failed: {}", ex.message)
        null
    } ?: return null

    val payload = jwt.parsePayload()
    val credentials = JWTCredential(payload)
    return validate(call, credentials)
}

private fun HttpAuthHeader.getBlob(schemes: JWTAuthSchemes) = when {
    this is HttpAuthHeader.Single && authScheme in schemes -> blob
    else -> null
}

private fun ApplicationRequest.parseAuthorizationHeaderOrNull() = try {
    parseAuthorizationHeader()
} catch (ex: IllegalArgumentException) {
    JWTLogger.trace("Illegal HTTP auth header", ex)
    null
}

internal fun Jwk.makeAlgorithm(): Algorithm = when (algorithm) {
    "RS256" -> Algorithm.RSA256(publicKey as RSAPublicKey, null)
    "RS384" -> Algorithm.RSA384(publicKey as RSAPublicKey, null)
    "RS512" -> Algorithm.RSA512(publicKey as RSAPublicKey, null)
    "ES256" -> Algorithm.ECDSA256(publicKey as ECPublicKey, null)
    "ES384" -> Algorithm.ECDSA384(publicKey as ECPublicKey, null)
    "ES512" -> Algorithm.ECDSA512(publicKey as ECPublicKey, null)
    null -> Algorithm.RSA256(publicKey as RSAPublicKey, null)
    else -> throw IllegalArgumentException("Unsupported algorithm $algorithm")
}

private fun DecodedJWT.parsePayload(): Payload {
    val payloadString = String(Base64.getUrlDecoder().decode(payload))
    return JWTParser().parsePayload(payloadString)
}
