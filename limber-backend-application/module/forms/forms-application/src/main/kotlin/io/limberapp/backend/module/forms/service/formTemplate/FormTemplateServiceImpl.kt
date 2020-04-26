package io.limberapp.backend.module.forms.service.formTemplate

import com.google.inject.Inject
import io.limberapp.backend.module.forms.model.formTemplate.FormTemplateModel
import io.limberapp.backend.module.forms.store.formTemplate.FormTemplateStore
import java.util.UUID

internal class FormTemplateServiceImpl @Inject constructor(
    private val formTemplateStore: FormTemplateStore
) : FormTemplateService {
    override fun create(model: FormTemplateModel) = formTemplateStore.create(model)

    override fun get(formTemplateGuid: UUID) = formTemplateStore.get(formTemplateGuid)

    override fun getByFeatureGuid(featureGuid: UUID) = formTemplateStore.getByFeatureGuid(featureGuid)

    override fun update(formTemplateGuid: UUID, update: FormTemplateModel.Update) =
        formTemplateStore.update(formTemplateGuid, update)

    override fun delete(formTemplateGuid: UUID) = formTemplateStore.delete(formTemplateGuid)
}
