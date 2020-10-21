package io.limberapp.backend.module.forms.endpoint.formTemplate

import com.google.inject.Inject
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.limberapp.backend.authorization.authorization.AuthFeatureMember
import io.limberapp.backend.endpoint.LimberApiEndpoint
import io.limberapp.backend.module.forms.api.formTemplate.FormTemplateApi
import io.limberapp.backend.module.forms.mapper.formTemplate.FormTemplateMapper
import io.limberapp.backend.module.forms.rep.formTemplate.FormTemplateRep
import io.limberapp.backend.module.forms.service.formTemplate.FormTemplateQuestionService
import io.limberapp.backend.module.forms.service.formTemplate.FormTemplateService
import io.limberapp.common.permissions.featurePermissions.feature.forms.FormsFeaturePermission
import io.limberapp.common.restInterface.template
import java.util.*

internal class PostFormTemplate @Inject constructor(
    application: Application,
    private val formTemplateService: FormTemplateService,
    private val formTemplateQuestionService: FormTemplateQuestionService,
    private val formTemplateMapper: FormTemplateMapper,
) : LimberApiEndpoint<FormTemplateApi.Post, FormTemplateRep.Complete>(
    application = application,
    endpointTemplate = FormTemplateApi.Post::class.template()
) {
  override suspend fun determineCommand(call: ApplicationCall) = FormTemplateApi.Post(
      featureGuid = call.parameters.getAsType(UUID::class, "featureGuid"),
      rep = call.getAndValidateBody()
  )

  override suspend fun Handler.handle(command: FormTemplateApi.Post): FormTemplateRep.Complete {
    val rep = command.rep.required()
    auth { AuthFeatureMember(command.featureGuid, permission = FormsFeaturePermission.MANAGE_FORM_TEMPLATES) }
    val formTemplate = formTemplateService.create(formTemplateMapper.model(command.featureGuid, rep))
    val questions = formTemplateQuestionService.getByFormTemplateGuid(
        featureGuid = command.featureGuid,
        formTemplateGuid = formTemplate.guid,
    )
    return formTemplateMapper.completeRep(formTemplate, questions)
  }
}
