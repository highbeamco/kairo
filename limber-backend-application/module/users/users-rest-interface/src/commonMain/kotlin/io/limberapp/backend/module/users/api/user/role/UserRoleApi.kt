package io.limberapp.backend.module.users.api.user.role

import com.piperframework.restInterface.HttpMethod
import com.piperframework.restInterface.PiperEndpoint
import com.piperframework.types.UUID
import com.piperframework.util.enc
import io.limberapp.backend.authorization.principal.JwtRole

@Suppress("StringLiteralDuplication")
object UserRoleApi {

    data class Put(val userId: UUID, val role: JwtRole) : PiperEndpoint(
        httpMethod = HttpMethod.PUT,
        path = "/users/${enc(userId)}/roles/${enc(role)}"
    )

    data class Delete(val userId: UUID, val role: JwtRole) : PiperEndpoint(
        httpMethod = HttpMethod.DELETE,
        path = "/users/${enc(userId)}/roles/${enc(role)}"
    )
}