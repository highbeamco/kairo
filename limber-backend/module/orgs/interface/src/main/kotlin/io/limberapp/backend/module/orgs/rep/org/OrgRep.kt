package io.limberapp.backend.module.orgs.rep.org

import io.limberapp.backend.module.orgs.rep.feature.FeatureRep
import io.limberapp.common.rep.CompleteRep
import io.limberapp.common.rep.CreationRep
import io.limberapp.common.rep.UpdateRep
import io.limberapp.common.validation.RepValidation
import io.limberapp.common.validation.Validator
import io.limberapp.common.validation.ifPresent
import java.time.ZonedDateTime
import java.util.*

object OrgRep {
  data class Creation(
      val name: String,
  ) : CreationRep {
    override fun validate() = RepValidation {
      validate(Creation::name) { Validator.orgName(value) }
    }
  }

  data class Complete(
      val guid: UUID,
      override val createdDate: ZonedDateTime,
      val name: String,
      val ownerUserGuid: UUID?,
      val features: List<FeatureRep.Complete>,
  ) : CompleteRep

  data class Update(
      val name: String? = null,
      val ownerUserGuid: UUID? = null,
  ) : UpdateRep {
    override fun validate() = RepValidation {
      validate(Update::name) { ifPresent { Validator.orgName(value) } }
    }
  }
}
