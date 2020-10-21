package io.limberapp.backend.module.forms.endpoint.formInstance.question

import com.google.inject.Inject
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.limberapp.backend.authorization.authorization.AuthFeatureMember
import io.limberapp.backend.authorization.authorization.AuthFormInstance
import io.limberapp.backend.endpoint.LimberApiEndpoint
import io.limberapp.backend.module.forms.api.formInstance.FormInstanceQuestionApi
import io.limberapp.backend.module.forms.exception.formInstance.FormInstanceNotFound
import io.limberapp.backend.module.forms.mapper.formInstance.FormInstanceQuestionMapper
import io.limberapp.backend.module.forms.rep.formInstance.FormInstanceQuestionRep
import io.limberapp.backend.module.forms.service.formInstance.FormInstanceQuestionService
import io.limberapp.backend.module.forms.service.formInstance.FormInstanceService
import io.limberapp.common.exception.unprocessableEntity.unprocessable
import io.limberapp.common.permissions.featurePermissions.feature.forms.FormsFeaturePermission
import io.limberapp.common.restInterface.template
import java.util.*

internal class PutFormInstanceQuestion @Inject constructor(
    application: Application,
    private val formInstanceService: FormInstanceService,
    private val formInstanceQuestionService: FormInstanceQuestionService,
    private val formInstanceQuestionMapper: FormInstanceQuestionMapper,
) : LimberApiEndpoint<FormInstanceQuestionApi.Put, FormInstanceQuestionRep.Complete>(
    application = application,
    endpointTemplate = FormInstanceQuestionApi.Put::class.template()
) {
  override suspend fun determineCommand(call: ApplicationCall) = FormInstanceQuestionApi.Put(
      featureGuid = call.parameters.getAsType(UUID::class, "featureGuid"),
      formInstanceGuid = call.parameters.getAsType(UUID::class, "formInstanceGuid"),
      questionGuid = call.parameters.getAsType(UUID::class, "questionGuid"),
      rep = call.getAndValidateBody()
  )

  override suspend fun Handler.handle(command: FormInstanceQuestionApi.Put): FormInstanceQuestionRep.Complete {
    val rep = command.rep.required()
    auth { AuthFeatureMember(command.featureGuid) }
    auth {
      AuthFormInstance(
          formInstance = formInstanceService.findOnlyOrNull {
            featureGuid(command.featureGuid)
            formInstanceGuid(command.formInstanceGuid)
          } ?: throw FormInstanceNotFound().unprocessable(),
          ifIsOwnFormInstance = FormsFeaturePermission.MODIFY_OWN_FORM_INSTANCES,
          ifIsNotOwnFormInstance = FormsFeaturePermission.MODIFY_OTHERS_FORM_INSTANCES,
      )
    }
    val formInstanceQuestion = formInstanceQuestionService.upsert(
        featureGuid = command.featureGuid,
        model = formInstanceQuestionMapper.model(command.formInstanceGuid, command.questionGuid, rep)
    )
    return formInstanceQuestionMapper.completeRep(formInstanceQuestion)
  }
}
