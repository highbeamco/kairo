package limber.endpoint.feature

import com.google.inject.Inject
import limber.auth.Auth
import limber.auth.OrganizationAuth
import limber.auth.auth
import limber.feature.rest.RestEndpointHandler
import limber.service.feature.FeatureService
import limber.api.feature.FeatureApi as Api
import limber.rep.feature.FeatureRep as Rep

public class GetFeaturesByOrganization @Inject internal constructor(
  private val featureService: FeatureService,
) : RestEndpointHandler<Api.GetByOrganization, List<Rep>>(Api.GetByOrganization::class) {
  override suspend fun handler(endpoint: Api.GetByOrganization): List<Rep> {
    auth(Auth.Permission("feature:read"))
    auth(OrganizationAuth(endpoint.organizationGuid)) { return@handler emptyList() }
    return featureService.getByOrganization(endpoint.organizationGuid)
  }
}
