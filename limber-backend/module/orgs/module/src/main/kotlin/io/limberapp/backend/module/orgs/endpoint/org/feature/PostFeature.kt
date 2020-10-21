package io.limberapp.backend.module.orgs.endpoint.org.feature

import com.google.inject.Inject
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.limberapp.backend.authorization.authorization.AuthOrgMember
import io.limberapp.backend.endpoint.LimberApiEndpoint
import io.limberapp.backend.module.orgs.api.feature.FeatureApi
import io.limberapp.backend.module.orgs.mapper.feature.FeatureMapper
import io.limberapp.backend.module.orgs.rep.feature.FeatureRep
import io.limberapp.backend.module.orgs.service.feature.FeatureService
import io.limberapp.common.permissions.orgPermissions.OrgPermission
import io.limberapp.common.restInterface.template
import java.util.*

internal class PostFeature @Inject constructor(
    application: Application,
    private val featureService: FeatureService,
    private val featureMapper: FeatureMapper,
) : LimberApiEndpoint<FeatureApi.Post, FeatureRep.Complete>(
    application = application,
    endpointTemplate = FeatureApi.Post::class.template()
) {
  override suspend fun determineCommand(call: ApplicationCall) = FeatureApi.Post(
      orgGuid = call.parameters.getAsType(UUID::class, "orgGuid"),
      rep = call.getAndValidateBody()
  )

  override suspend fun Handler.handle(command: FeatureApi.Post): FeatureRep.Complete {
    val rep = command.rep.required()
    auth { AuthOrgMember(command.orgGuid, permission = OrgPermission.MANAGE_ORG_FEATURES) }
    val feature = featureService.create(featureMapper.model(command.orgGuid, rep))
    return featureMapper.completeRep(feature)
  }
}
