package io.limberapp.backend.module.forms.rep.formTemplate.formTemplateQuestion

import io.limberapp.backend.module.forms.rep.formTemplate.FormTemplateQuestionRep
import io.limberapp.common.serialization.serializer.LocalDateSerializer
import io.limberapp.common.serialization.serializer.LocalDateTimeSerializer
import io.limberapp.common.serialization.serializer.UuidSerializer
import io.limberapp.common.types.LocalDate
import io.limberapp.common.types.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

object FormTemplateDateQuestionRep {
  @Serializable
  @SerialName("DATE")
  data class Creation(
    override val label: String,
    override val helpText: String? = null,
    override val required: Boolean,
    @Serializable(with = LocalDateSerializer::class)
    val earliest: LocalDate? = null,
    @Serializable(with = LocalDateSerializer::class)
    val latest: LocalDate? = null,
  ) : FormTemplateQuestionRep.Creation

  @Serializable
  @SerialName("DATE")
  data class Complete(
    @Serializable(with = UuidSerializer::class)
    override val guid: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    override val createdDate: LocalDateTime,
    override val label: String,
    override val helpText: String?,
    override val required: Boolean,
    @Serializable(with = LocalDateSerializer::class)
    val earliest: LocalDate?,
    @Serializable(with = LocalDateSerializer::class)
    val latest: LocalDate?,
  ) : FormTemplateQuestionRep.Complete

  @Serializable
  @SerialName("DATE")
  data class Update(
    override val label: String? = null,
    override val helpText: String? = null,
    override val required: Boolean? = null,
    @Serializable(with = LocalDateSerializer::class)
    val earliest: LocalDate? = null,
    @Serializable(with = LocalDateSerializer::class)
    val latest: LocalDate? = null,
  ) : FormTemplateQuestionRep.Update
}
