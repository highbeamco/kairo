package limber.endpoint.feature

import com.google.inject.Inject
import limber.feature.rest.RestEndpointHandler
import limber.service.feature.FeatureService
import limber.api.feature.FeatureApi as Api
import limber.rep.feature.FeatureRep as Rep

public class GetFeature @Inject internal constructor(
  private val featureService: FeatureService,
) : RestEndpointHandler<Api.Get, Rep?>(Api.Get::class) {
  override suspend fun handler(endpoint: Api.Get): Rep? {
    return featureService.get(endpoint.organizationGuid, endpoint.featureGuid)
  }
}