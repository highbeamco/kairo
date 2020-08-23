package io.limberapp.backend.module.forms.endpoint.formTemplate

import com.google.inject.Inject
import com.piperframework.config.serving.ServingConfig
import com.piperframework.restInterface.template
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.limberapp.backend.authorization.Authorization
import io.limberapp.backend.endpoint.LimberApiEndpoint
import io.limberapp.backend.module.forms.api.formTemplate.FormTemplateApi
import io.limberapp.backend.module.forms.exception.formTemplate.FormTemplateNotFound
import io.limberapp.backend.module.forms.mapper.formTemplate.FormTemplateMapper
import io.limberapp.backend.module.forms.rep.formTemplate.FormTemplateRep
import io.limberapp.backend.module.forms.service.formTemplate.FormTemplateQuestionService
import io.limberapp.backend.module.forms.service.formTemplate.FormTemplateService
import java.util.*

internal class GetFormTemplate @Inject constructor(
  application: Application,
  servingConfig: ServingConfig,
  private val formTemplateService: FormTemplateService,
  private val formTemplateQuestionService: FormTemplateQuestionService,
  private val formTemplateMapper: FormTemplateMapper,
) : LimberApiEndpoint<FormTemplateApi.Get, FormTemplateRep.Complete>(
  application = application,
  pathPrefix = servingConfig.apiPathPrefix,
  endpointTemplate = FormTemplateApi.Get::class.template()
) {
  override suspend fun determineCommand(call: ApplicationCall) = FormTemplateApi.Get(
    featureGuid = call.parameters.getAsType(UUID::class, "featureGuid"),
    formTemplateGuid = call.parameters.getAsType(UUID::class, "formTemplateGuid")
  )

  override suspend fun Handler.handle(command: FormTemplateApi.Get): FormTemplateRep.Complete {
    Authorization.FeatureMember(command.featureGuid).authorize()
    val formTemplate = formTemplateService.findOnlyOrNull {
      featureGuid(command.featureGuid)
      formTemplateGuid(command.formTemplateGuid)
    } ?: throw FormTemplateNotFound()
    val questions = formTemplateQuestionService.getByFormTemplateGuid(command.featureGuid, command.formTemplateGuid)
    return formTemplateMapper.completeRep(formTemplate, questions)
  }
}
