package io.limberapp.backend.module.forms.endpoint.formTemplate.question

import com.google.inject.Inject
import com.piperframework.config.serving.ServingConfig
import com.piperframework.restInterface.template
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.limberapp.backend.authorization.Authorization
import io.limberapp.backend.endpoint.LimberApiEndpoint
import io.limberapp.backend.module.forms.api.formTemplate.question.FormTemplateQuestionApi
import io.limberapp.backend.module.forms.mapper.formTemplate.FormTemplateQuestionMapper
import io.limberapp.backend.module.forms.rep.formTemplate.FormTemplateQuestionRep
import io.limberapp.backend.module.forms.service.formTemplate.FormTemplateQuestionService
import java.util.*

/**
 * Creates a new question within a form template.
 */
internal class PostFormTemplateQuestion @Inject constructor(
  application: Application,
  servingConfig: ServingConfig,
  private val formTemplateQuestionService: FormTemplateQuestionService,
  private val formTemplateQuestionMapper: FormTemplateQuestionMapper
) : LimberApiEndpoint<FormTemplateQuestionApi.Post, FormTemplateQuestionRep.Complete>(
  application = application,
  pathPrefix = servingConfig.apiPathPrefix,
  endpointTemplate = FormTemplateQuestionApi.Post::class.template()
) {
  override suspend fun determineCommand(call: ApplicationCall) = FormTemplateQuestionApi.Post(
    featureGuid = call.parameters.getAsType(UUID::class, "featureGuid"),
    formTemplateGuid = call.parameters.getAsType(UUID::class, "formTemplateGuid"),
    rank = call.parameters.getAsType(Int::class, "rank", optional = true),
    rep = call.getAndValidateBody()
  )

  override suspend fun Handler.handle(command: FormTemplateQuestionApi.Post): FormTemplateQuestionRep.Complete {
    val rep = command.rep.required()
    Authorization.HasAccessToFeature(command.featureGuid).authorize()
    val formTemplateQuestion = formTemplateQuestionService.create(
      featureGuid = command.featureGuid,
      model = formTemplateQuestionMapper.model(command.formTemplateGuid, rep),
      rank = command.rank
    )
    return formTemplateQuestionMapper.completeRep(formTemplateQuestion)
  }
}
