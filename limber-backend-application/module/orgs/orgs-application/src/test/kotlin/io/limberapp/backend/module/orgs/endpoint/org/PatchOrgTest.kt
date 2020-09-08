package io.limberapp.backend.module.orgs.endpoint.org

import com.piperframework.testing.responseContent
import io.limberapp.backend.module.orgs.api.org.OrgApi
import io.limberapp.backend.module.orgs.exception.org.OrgNotFound
import io.limberapp.backend.module.orgs.rep.org.OrgRep
import io.limberapp.backend.module.orgs.testing.ResourceTest
import io.limberapp.backend.module.orgs.testing.fixtures.org.OrgRepFixtures
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

internal class PatchOrgTest : ResourceTest() {
  @Test
  fun doesNotExist() {
    val orgGuid = UUID.randomUUID()

    piperTest.test(
      endpoint = OrgApi.Patch(orgGuid, OrgRep.Update("Standing Teeth")),
      expectedException = OrgNotFound()
    )
  }

  @Test
  fun happyPathName() {
    var orgRep = OrgRepFixtures.crankyPastaFixture.complete(this, 0)
    piperTest.setup(OrgApi.Post(OrgRepFixtures.crankyPastaFixture.creation()))

    orgRep = orgRep.copy(name = "Standing Teeth")
    piperTest.test(OrgApi.Patch(orgRep.guid, OrgRep.Update(name = "Standing Teeth"))) {
      val actual = json.parse<OrgRep.Complete>(responseContent)
      assertEquals(orgRep, actual)
    }

    piperTest.test(OrgApi.Get(orgRep.guid)) {
      val actual = json.parse<OrgRep.Complete>(responseContent)
      assertEquals(orgRep, actual)
    }
  }

  @Test
  fun happyPathOwnerUserGuid() {
    val ownerUserGuid = UUID.randomUUID()

    var orgRep = OrgRepFixtures.crankyPastaFixture.complete(this, 0)
    piperTest.setup(OrgApi.Post(OrgRepFixtures.crankyPastaFixture.creation()))

    orgRep = orgRep.copy(ownerUserGuid = ownerUserGuid)
    piperTest.test(OrgApi.Patch(orgRep.guid, OrgRep.Update(ownerUserGuid = ownerUserGuid))) {
      val actual = json.parse<OrgRep.Complete>(responseContent)
      assertEquals(orgRep, actual)
    }

    piperTest.test(OrgApi.Get(orgRep.guid)) {
      val actual = json.parse<OrgRep.Complete>(responseContent)
      assertEquals(orgRep, actual)
    }
  }
}
