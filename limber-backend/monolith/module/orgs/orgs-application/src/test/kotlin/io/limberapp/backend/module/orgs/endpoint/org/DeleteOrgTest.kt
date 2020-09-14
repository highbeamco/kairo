package io.limberapp.backend.module.orgs.endpoint.org

import io.limberapp.backend.module.orgs.api.org.OrgApi
import io.limberapp.backend.module.orgs.exception.org.OrgNotFound
import io.limberapp.backend.module.orgs.testing.ResourceTest
import io.limberapp.backend.module.orgs.testing.fixtures.org.OrgRepFixtures
import org.junit.jupiter.api.Test
import java.util.*

internal class DeleteOrgTest : ResourceTest() {
  @Test
  fun doesNotExist() {
    val orgGuid = UUID.randomUUID()

    limberTest.test(
      endpoint = OrgApi.Delete(orgGuid),
      expectedException = OrgNotFound()
    )
  }

  @Test
  fun happyPath() {
    val orgRep = OrgRepFixtures.crankyPastaFixture.complete(this, 0)
    limberTest.setup(OrgApi.Post(OrgRepFixtures.crankyPastaFixture.creation()))

    limberTest.test(OrgApi.Delete(orgRep.guid)) {}

    limberTest.test(
      endpoint = OrgApi.Get(orgRep.guid),
      expectedException = OrgNotFound()
    )
  }
}
