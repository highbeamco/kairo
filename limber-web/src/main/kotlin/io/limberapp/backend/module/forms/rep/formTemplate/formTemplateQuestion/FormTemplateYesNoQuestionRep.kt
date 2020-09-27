package io.limberapp.backend.module.forms.rep.formTemplate.formTemplateQuestion

import io.limberapp.backend.module.forms.rep.formTemplate.FormTemplateQuestionRep
import io.limberapp.common.serialization.serializer.LocalDateTimeSerializer
import io.limberapp.common.serialization.serializer.UuidSerializer
import io.limberapp.common.types.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

object FormTemplateYesNoQuestionRep {
  @Serializable
  @SerialName("YES_NO")
  data class Creation(
    override val label: String,
    override val helpText: String? = null,
    override val required: Boolean,
  ) : FormTemplateQuestionRep.Creation

  @Serializable
  @SerialName("YES_NO")
  data class Complete(
    @Serializable(with = UuidSerializer::class)
    override val guid: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    override val createdDate: LocalDateTime,
    override val label: String,
    override val helpText: String?,
    override val required: Boolean,
  ) : FormTemplateQuestionRep.Complete

  @Serializable
  @SerialName("YES_NO")
  data class Update(
    override val label: String? = null,
    override val helpText: String? = null,
    override val required: Boolean? = null,
  ) : FormTemplateQuestionRep.Update
}