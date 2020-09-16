package io.limberapp.backend.module.forms.rep.formInstance.formInstanceQuestion

import io.limberapp.backend.module.forms.rep.formInstance.FormInstanceQuestionRep
import io.limberapp.common.serialization.serializer.LocalDateSerializer
import io.limberapp.common.serialization.serializer.LocalDateTimeSerializer
import io.limberapp.common.serialization.serializer.UuidSerializer
import io.limberapp.common.types.LocalDate
import io.limberapp.common.types.LocalDateTime
import io.limberapp.common.validation.RepValidation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

object FormInstanceDateQuestionRep {
  @Serializable
  @SerialName("DATE")
  data class Creation(
    @Serializable(with = LocalDateSerializer::class)
    val date: LocalDate,
  ) : FormInstanceQuestionRep.Creation {
    override fun validate() = RepValidation {}
  }

  @Serializable
  @SerialName("DATE")
  data class Complete(
    @Serializable(with = LocalDateTimeSerializer::class)
    override val createdDate: LocalDateTime,
    @Serializable(with = UuidSerializer::class)
    override val questionGuid: String?,
    @Serializable(with = LocalDateSerializer::class)
    val date: LocalDate,
  ) : FormInstanceQuestionRep.Complete
}
