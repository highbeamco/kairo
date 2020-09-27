package io.limberapp.backend.module.auth.model.tenant

import java.time.LocalDateTime
import java.util.*

data class TenantModel(
  val createdDate: LocalDateTime,
  val orgGuid: UUID,
  val name: String,
  val auth0ClientId: String,
) {
  data class Update(
    val name: String?,
    val auth0ClientId: String?,
  )
}