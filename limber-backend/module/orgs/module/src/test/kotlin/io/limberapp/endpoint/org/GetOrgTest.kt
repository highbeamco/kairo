package io.limberapp.endpoint.org

import io.ktor.server.testing.TestApplicationEngine
import io.limberapp.api.feature.FeatureApi
import io.limberapp.api.org.OrgApi
import io.limberapp.rep.feature.FeatureRepFixtures
import io.limberapp.rep.org.OrgRepFixtures
import io.limberapp.server.Server
import io.limberapp.testing.integration.IntegrationTest
import org.junit.jupiter.api.Test
import java.util.UUID

internal class GetOrgTest(
    engine: TestApplicationEngine,
    server: Server<*>,
) : IntegrationTest(engine, server) {
  @Test
  fun `org does not exist`() {
    val orgGuid = UUID.randomUUID()

    test(expectResult = null) { orgClient(OrgApi.Get(orgGuid)) }
  }

  @Test
  fun `org exists`() {
    var orgRep = OrgRepFixtures.crankyPastaFixture.complete(this, 0)
    setup { orgClient(OrgApi.Post(OrgRepFixtures.crankyPastaFixture.creation())) }

    val featureRep = FeatureRepFixtures.homeFixture.complete(this, orgRep.guid, 1)
    orgRep = orgRep.copy(features = listOf(featureRep))
    setup { featureClient(FeatureApi.Post(FeatureRepFixtures.homeFixture.creation(orgRep.guid))) }

    test(expectResult = orgRep) { orgClient(OrgApi.Get(orgRep.guid)) }
  }
}
