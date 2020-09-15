package io.limberapp.backend.module.forms.service.formInstance

import com.google.inject.Inject
import io.limberapp.backend.module.forms.exception.formInstance.CannotDeleteRequiredQuestion
import io.limberapp.backend.module.forms.exception.formInstance.FormInstanceQuestionNotFound
import io.limberapp.backend.module.forms.model.formInstance.FormInstanceQuestionFinder
import io.limberapp.backend.module.forms.model.formInstance.FormInstanceQuestionModel
import io.limberapp.backend.module.forms.service.formTemplate.FormTemplateQuestionService
import io.limberapp.backend.module.forms.store.formInstance.FormInstanceQuestionStore
import io.limberapp.common.finder.Finder
import java.util.*

internal class FormInstanceQuestionServiceImpl @Inject constructor(
  private val formTemplateQuestionService: FormTemplateQuestionService,
  private val formInstanceService: FormInstanceService,
  private val formInstanceQuestionStore: FormInstanceQuestionStore,
) : FormInstanceQuestionService,
  Finder<FormInstanceQuestionModel, FormInstanceQuestionFinder> by formInstanceQuestionStore {
  override fun upsert(featureGuid: UUID, model: FormInstanceQuestionModel): FormInstanceQuestionModel {
    requireNotNull(model.questionGuid)
    return formInstanceQuestionStore.upsert(featureGuid, model)
  }

  override fun delete(featureGuid: UUID, formInstanceGuid: UUID, questionGuid: UUID) {
    val formInstance = formInstanceService.findOnlyOrNull {
      featureGuid(featureGuid)
      formInstanceGuid(formInstanceGuid)
    } ?: throw FormInstanceQuestionNotFound()
    val formTemplateQuestion = formTemplateQuestionService.findOnlyOrNull {
      featureGuid(featureGuid)
      formTemplateGuid(formInstance.formTemplateGuid)
      questionGuid(questionGuid)
    } ?: throw FormInstanceQuestionNotFound()
    if (formTemplateQuestion.required && formInstance.submittedDate != null) throw CannotDeleteRequiredQuestion()
    formInstanceQuestionStore.delete(featureGuid, formInstanceGuid, questionGuid)
  }
}