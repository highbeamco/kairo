package io.limberapp.backend.module.orgs.endpoint.org

import io.ktor.server.testing.TestApplicationEngine
import io.limberapp.backend.module.orgs.api.org.OrgApi
import io.limberapp.backend.module.orgs.exception.org.OrgNotFound
import io.limberapp.backend.module.orgs.rep.org.OrgRep
import io.limberapp.backend.module.orgs.testing.IntegrationTest
import io.limberapp.backend.module.orgs.testing.fixtures.org.OrgRepFixtures
import io.limberapp.common.LimberApplication
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

internal class GetOrgByOwnerUserGuidTest(
  engine: TestApplicationEngine,
  limberServer: LimberApplication<*>,
) : IntegrationTest(engine, limberServer) {
  @Test
  fun noOrg() {
    val ownerUserGuid = UUID.randomUUID()

    test(
      endpoint = OrgApi.GetByOwnerUserGuid(ownerUserGuid),
      expectedException = OrgNotFound()
    )
  }

  @Test
  fun happyPath() {
    val ownerUserGuid = UUID.randomUUID()

    var crankyPastaOrgRep = OrgRepFixtures.crankyPastaFixture.complete(this, 0)
    setup(OrgApi.Post(OrgRepFixtures.crankyPastaFixture.creation()))

    crankyPastaOrgRep = crankyPastaOrgRep.copy(ownerUserGuid = ownerUserGuid)
    setup(OrgApi.Patch(crankyPastaOrgRep.guid, OrgRep.Update(ownerUserGuid = ownerUserGuid)))

    test(OrgApi.GetByOwnerUserGuid(ownerUserGuid)) {
      val actual = json.parse<OrgRep.Complete>(responseContent)
      assertEquals(crankyPastaOrgRep, actual)
    }
  }
}