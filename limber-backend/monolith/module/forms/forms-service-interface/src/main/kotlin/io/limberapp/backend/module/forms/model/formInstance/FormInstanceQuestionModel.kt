package io.limberapp.backend.module.forms.model.formInstance

import io.limberapp.backend.module.forms.model.formTemplate.FormTemplateQuestionModel
import java.time.LocalDateTime
import java.util.*

interface FormInstanceQuestionModel {
  val createdDate: LocalDateTime
  val formInstanceGuid: UUID
  val questionGuid: UUID?
  val type: FormTemplateQuestionModel.Type
}