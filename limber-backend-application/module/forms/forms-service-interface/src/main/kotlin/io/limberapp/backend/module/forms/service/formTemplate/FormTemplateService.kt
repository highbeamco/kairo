package io.limberapp.backend.module.forms.service.formTemplate

import com.piperframework.finder.Finder
import io.limberapp.backend.LimberModule
import io.limberapp.backend.module.forms.model.formTemplate.FormTemplateFinder
import io.limberapp.backend.module.forms.model.formTemplate.FormTemplateModel
import java.util.*

@LimberModule.Forms
interface FormTemplateService : Finder<FormTemplateModel, FormTemplateFinder> {
  fun create(model: FormTemplateModel): FormTemplateModel

  fun update(featureGuid: UUID, formTemplateGuid: UUID, update: FormTemplateModel.Update): FormTemplateModel

  fun delete(featureGuid: UUID, formTemplateGuid: UUID)
}
