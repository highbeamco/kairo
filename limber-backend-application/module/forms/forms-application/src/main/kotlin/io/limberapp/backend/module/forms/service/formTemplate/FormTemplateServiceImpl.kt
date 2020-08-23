package io.limberapp.backend.module.forms.service.formTemplate

import com.google.inject.Inject
import com.piperframework.finder.Finder
import io.limberapp.backend.module.forms.model.formTemplate.FormTemplateFinder
import io.limberapp.backend.module.forms.model.formTemplate.FormTemplateModel
import io.limberapp.backend.module.forms.store.formTemplate.FormTemplateStore
import java.util.*

internal class FormTemplateServiceImpl @Inject constructor(
  private val formTemplateQuestionService: FormTemplateQuestionService,
  private val formTemplateStore: FormTemplateStore,
) : FormTemplateService, Finder<FormTemplateModel, FormTemplateFinder> by formTemplateStore {
  override fun create(model: FormTemplateModel): FormTemplateModel {
    val formTemplate = formTemplateStore.create(model)
    formTemplateQuestionService.createDefaults(model.featureGuid, model.guid)
    return formTemplate
  }

  override fun update(featureGuid: UUID, formTemplateGuid: UUID, update: FormTemplateModel.Update) =
    formTemplateStore.update(featureGuid, formTemplateGuid, update)

  override fun delete(featureGuid: UUID, formTemplateGuid: UUID) =
    formTemplateStore.delete(featureGuid, formTemplateGuid)
}
