package io.limberapp.backend.module.orgs.endpoint.org

import io.limberapp.backend.module.orgs.api.org.OrgApi
import io.limberapp.backend.module.orgs.rep.org.OrgRep
import io.limberapp.backend.module.orgs.testing.ResourceTest
import io.limberapp.backend.module.orgs.testing.fixtures.org.OrgRepFixtures
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class PostOrgTest : ResourceTest() {
  @Test
  fun happyPath() {
    val orgRep = OrgRepFixtures.crankyPastaFixture.complete(this, 0)
    piperTest.test(OrgApi.Post(OrgRepFixtures.crankyPastaFixture.creation())) {
      val actual = json.parse<OrgRep.Complete>(response.content!!)
      assertEquals(orgRep, actual)
    }

    piperTest.test(OrgApi.Get(orgRep.guid)) {
      val actual = json.parse<OrgRep.Complete>(response.content!!)
      assertEquals(orgRep, actual)
    }
  }
}
