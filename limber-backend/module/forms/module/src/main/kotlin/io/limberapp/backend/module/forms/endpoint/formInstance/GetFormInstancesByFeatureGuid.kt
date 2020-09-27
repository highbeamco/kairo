package io.limberapp.backend.module.forms.endpoint.formInstance

import com.google.inject.Inject
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.limberapp.backend.authorization.Authorization
import io.limberapp.backend.authorization.permissions.featurePermissions.feature.forms.FormsFeaturePermission
import io.limberapp.backend.endpoint.LimberApiEndpoint
import io.limberapp.backend.module.forms.api.formInstance.FormInstanceApi
import io.limberapp.backend.module.forms.mapper.formInstance.FormInstanceMapper
import io.limberapp.backend.module.forms.model.formInstance.FormInstanceFinder
import io.limberapp.backend.module.forms.rep.formInstance.FormInstanceRep
import io.limberapp.backend.module.forms.service.formInstance.FormInstanceService
import io.limberapp.common.finder.SortableFinder
import io.limberapp.common.restInterface.template
import java.util.*

internal class GetFormInstancesByFeatureGuid @Inject constructor(
  application: Application,
  private val formInstanceService: FormInstanceService,
  private val formInstanceMapper: FormInstanceMapper,
) : LimberApiEndpoint<FormInstanceApi.GetByFeatureGuid, List<FormInstanceRep.Summary>>(
  application = application,
  endpointTemplate = FormInstanceApi.GetByFeatureGuid::class.template()
) {
  override suspend fun determineCommand(call: ApplicationCall) = FormInstanceApi.GetByFeatureGuid(
    featureGuid = call.parameters.getAsType(UUID::class, "featureGuid"),
    creatorAccountGuid = call.parameters.getAsType(UUID::class, "creatorAccountGuid", optional = true)
  )

  override suspend fun Handler.handle(command: FormInstanceApi.GetByFeatureGuid): List<FormInstanceRep.Summary> {
    Authorization.FeatureMember(command.featureGuid).authorize()
    if (command.creatorAccountGuid == null || command.creatorAccountGuid != principal?.user?.guid) {
      Authorization.FeatureMemberWithFeaturePermission(
        featureGuid = command.featureGuid,
        featurePermission = FormsFeaturePermission.SEE_OTHERS_FORM_INSTANCES
      ).authorize()
    }
    val formInstances = formInstanceService.findAsSet {
      featureGuid(command.featureGuid)
      command.creatorAccountGuid?.let { creatorAccountGuid(it) }
      sortBy(FormInstanceFinder.SortBy.NUMBER, SortableFinder.SortDirection.DESCENDING)
    }
    return formInstances.map { formInstanceMapper.summaryRep(it) }
  }
}