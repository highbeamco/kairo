package io.limberapp.backend.module.auth.model.tenant

import java.time.LocalDateTime
import java.util.UUID

data class TenantModel(
    val domain: String,
    val created: LocalDateTime,
    val orgId: UUID,
    val auth0ClientId: String
) {

    data class Update(
        val domain: String?,
        val orgId: UUID?,
        val auth0ClientId: String?
    )
}