package io.limberapp.backend.module.orgs.endpoint.org.feature

import com.google.inject.Inject
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.limberapp.backend.authorization.Authorization
import io.limberapp.backend.authorization.permissions.orgPermissions.OrgPermission
import io.limberapp.backend.endpoint.LimberApiEndpoint
import io.limberapp.backend.module.orgs.api.org.feature.OrgFeatureApi
import io.limberapp.backend.module.orgs.mapper.feature.FeatureMapper
import io.limberapp.backend.module.orgs.rep.org.FeatureRep
import io.limberapp.backend.module.orgs.service.org.FeatureService
import io.limberapp.common.restInterface.template
import java.util.*

internal class PostFeature @Inject constructor(
  application: Application,
  private val featureService: FeatureService,
  private val featureMapper: FeatureMapper,
) : LimberApiEndpoint<OrgFeatureApi.Post, FeatureRep.Complete>(
  application = application,
  endpointTemplate = OrgFeatureApi.Post::class.template()
) {
  override suspend fun determineCommand(call: ApplicationCall) = OrgFeatureApi.Post(
    orgGuid = call.parameters.getAsType(UUID::class, "orgGuid"),
    rep = call.getAndValidateBody()
  )

  override suspend fun Handler.handle(command: OrgFeatureApi.Post): FeatureRep.Complete {
    val rep = command.rep.required()
    Authorization.OrgMemberWithPermission(command.orgGuid, OrgPermission.MANAGE_ORG_FEATURES).authorize()
    val feature = featureService.create(featureMapper.model(command.orgGuid, rep))
    return featureMapper.completeRep(feature)
  }
}
