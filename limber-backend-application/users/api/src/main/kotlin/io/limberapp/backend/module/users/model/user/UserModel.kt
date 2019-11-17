package io.limberapp.backend.module.users.model.user

import io.limberapp.framework.endpoint.authorization.jwt.JwtRole
import java.time.LocalDateTime
import java.util.UUID

data class UserModel(
    val id: UUID,
    val created: LocalDateTime,
    val version: Int,
    val firstName: String?,
    val lastName: String?,
    val emailAddress: String,
    val profilePhotoUrl: String?,
    val roles: Set<JwtRole>
) {

    data class Update(
        val firstName: String?,
        val lastName: String?
    )
}
