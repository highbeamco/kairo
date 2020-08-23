package io.limberapp.backend.module.forms.service.formInstance

import com.google.inject.Inject
import com.piperframework.finder.Finder
import io.limberapp.backend.module.forms.exception.formTemplate.FormTemplateNotFound
import io.limberapp.backend.module.forms.model.formInstance.FormInstanceFinder
import io.limberapp.backend.module.forms.model.formInstance.FormInstanceModel
import io.limberapp.backend.module.forms.store.formInstance.FormInstanceStore
import java.util.*

internal class FormInstanceServiceImpl @Inject constructor(
  private val formInstanceStore: FormInstanceStore,
) : FormInstanceService, Finder<FormInstanceModel, FormInstanceFinder> by formInstanceStore {
  override fun create(model: FormInstanceModel) =
    formInstanceStore.create(model)

  override fun update(featureGuid: UUID, formInstanceGuid: UUID, update: FormInstanceModel.Update): FormInstanceModel {
    formInstanceStore.findOnlyOrNull { featureGuid(featureGuid); formInstanceGuid(formInstanceGuid) }
      .ifNull { throw FormTemplateNotFound() }
    return formInstanceStore.update(formInstanceGuid, update)
  }

  override fun delete(featureGuid: UUID, formInstanceGuid: UUID) {
    formInstanceStore.findOnlyOrNull { featureGuid(featureGuid); formInstanceGuid(formInstanceGuid) }
      .ifNull { throw FormTemplateNotFound() }
    formInstanceStore.delete(formInstanceGuid)
  }
}
