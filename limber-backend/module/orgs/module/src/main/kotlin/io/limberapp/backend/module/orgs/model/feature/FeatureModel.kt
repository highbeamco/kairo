package io.limberapp.backend.module.orgs.model.feature

import java.time.ZonedDateTime
import java.util.*

data class FeatureModel(
    val guid: UUID,
    val createdDate: ZonedDateTime,
    val orgGuid: UUID,
    val rank: Int,
    val name: String,
    val path: String,
    val type: Type,
    val isDefaultFeature: Boolean,
) {
  enum class Type {
    FORMS,
    HOME;
  }

  data class Update(
      val rank: Int?,
      val name: String?,
      val path: String?,
      val isDefaultFeature: Boolean?,
  )
}