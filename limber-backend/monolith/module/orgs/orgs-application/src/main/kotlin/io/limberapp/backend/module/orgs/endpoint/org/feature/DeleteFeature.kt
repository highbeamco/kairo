package io.limberapp.backend.module.orgs.endpoint.org.feature

import com.google.inject.Inject
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.limberapp.backend.authorization.Authorization
import io.limberapp.backend.authorization.permissions.orgPermissions.OrgPermission
import io.limberapp.backend.endpoint.LimberApiEndpoint
import io.limberapp.backend.module.orgs.api.org.feature.OrgFeatureApi
import io.limberapp.backend.module.orgs.service.org.FeatureService
import io.limberapp.common.restInterface.template
import java.util.*

internal class DeleteFeature @Inject constructor(
  application: Application,
  private val featureService: FeatureService,
) : LimberApiEndpoint<OrgFeatureApi.Delete, Unit>(
  application = application,
  endpointTemplate = OrgFeatureApi.Delete::class.template()
) {
  override suspend fun determineCommand(call: ApplicationCall) = OrgFeatureApi.Delete(
    orgGuid = call.parameters.getAsType(UUID::class, "orgGuid"),
    featureGuid = call.parameters.getAsType(UUID::class, "featureGuid")
  )

  override suspend fun Handler.handle(command: OrgFeatureApi.Delete) {
    Authorization.OrgMemberWithPermission(command.orgGuid, OrgPermission.MANAGE_ORG_FEATURES).authorize()
    featureService.delete(
      orgGuid = command.orgGuid,
      featureGuid = command.featureGuid
    )
  }
}