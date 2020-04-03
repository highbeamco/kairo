package io.limberapp.backend.test

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTCreator
import com.auth0.jwt.algorithms.Algorithm
import com.piperframework.serialization.stringify
import com.piperframework.testing.PiperTest
import io.ktor.application.Application
import io.ktor.http.auth.HttpAuthHeader
import io.limberapp.backend.authorization.principal.Claims
import io.limberapp.backend.authorization.principal.Jwt
import io.limberapp.backend.authorization.principal.JwtRole
import io.limberapp.backend.authorization.principal.JwtUser
import kotlinx.serialization.builtins.ListSerializer
import java.util.UUID

class LimberTest(moduleFunction: Application.() -> Unit) : PiperTest(moduleFunction) {

    override fun createAuthHeader(): HttpAuthHeader? {
        val jwt = JWT.create().withJwt(
            jwt = Jwt(
                org = null,
                roles = listOf(JwtRole.SUPERUSER),
                user = JwtUser(UUID.randomUUID(), "Jeff", "Hudson")
            )
        ).sign(Algorithm.none())
        return HttpAuthHeader.Single("Bearer", jwt)
    }

    private fun JWTCreator.Builder.withJwt(jwt: Jwt): JWTCreator.Builder {
        withClaim(Claims.org, jwt.org?.let { json.stringify(it) })
        withClaim(Claims.roles, json.stringify(ListSerializer(JwtRole), jwt.roles))
        withClaim(Claims.user, json.stringify(jwt.user))
        return this
    }
}
