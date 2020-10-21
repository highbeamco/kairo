package io.limberapp.backend.module.auth.rep.feature

import io.limberapp.common.rep.CompleteRep
import io.limberapp.common.rep.CreationRep
import io.limberapp.common.rep.UpdateRep
import io.limberapp.common.validation.RepValidation
import io.limberapp.permissions.featurePermissions.FeaturePermissions
import java.time.ZonedDateTime
import java.util.*

object FeatureRoleRep {
  data class Creation(
      val orgRoleGuid: UUID,
      val permissions: FeaturePermissions,
  ) : CreationRep {
    override fun validate() = RepValidation {}
  }

  data class Complete(
      val guid: UUID,
      override val createdDate: ZonedDateTime,
      val orgRoleGuid: UUID,
      val permissions: FeaturePermissions,
  ) : CompleteRep

  data class Update(
      val permissions: FeaturePermissions? = null,
  ) : UpdateRep {
    override fun validate() = RepValidation {}
  }
}
