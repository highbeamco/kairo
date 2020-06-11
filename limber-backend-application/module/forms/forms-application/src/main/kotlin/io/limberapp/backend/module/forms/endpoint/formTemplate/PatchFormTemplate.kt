package io.limberapp.backend.module.forms.endpoint.formTemplate

import com.google.inject.Inject
import com.piperframework.config.serving.ServingConfig
import com.piperframework.restInterface.template
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.limberapp.backend.authorization.Authorization
import io.limberapp.backend.endpoint.LimberApiEndpoint
import io.limberapp.backend.module.forms.api.formTemplate.FormTemplateApi
import io.limberapp.backend.module.forms.mapper.formTemplate.FormTemplateMapper
import io.limberapp.backend.module.forms.rep.formTemplate.FormTemplateRep
import io.limberapp.backend.module.forms.service.formTemplate.FormTemplateService
import java.util.*

/**
 * Updates an form template's information.
 */
internal class PatchFormTemplate @Inject constructor(
  application: Application,
  servingConfig: ServingConfig,
  private val formTemplateService: FormTemplateService,
  private val formTemplateMapper: FormTemplateMapper
) : LimberApiEndpoint<FormTemplateApi.Patch, FormTemplateRep.Summary>(
  application = application,
  pathPrefix = servingConfig.apiPathPrefix,
  endpointTemplate = FormTemplateApi.Patch::class.template()
) {
  override suspend fun determineCommand(call: ApplicationCall) = FormTemplateApi.Patch(
    featureGuid = call.parameters.getAsType(UUID::class, "featureGuid"),
    formTemplateGuid = call.parameters.getAsType(UUID::class, "formTemplateGuid"),
    rep = call.getAndValidateBody()
  )

  override suspend fun Handler.handle(command: FormTemplateApi.Patch): FormTemplateRep.Summary {
    val rep = command.rep.required()
    Authorization.HasAccessToFeature(command.featureGuid).authorize()
    val formTemplate = formTemplateService.update(
      featureGuid = command.featureGuid,
      formTemplateGuid = command.formTemplateGuid,
      update = formTemplateMapper.update(rep)
    )
    return formTemplateMapper.summaryRep(formTemplate)
  }
}
