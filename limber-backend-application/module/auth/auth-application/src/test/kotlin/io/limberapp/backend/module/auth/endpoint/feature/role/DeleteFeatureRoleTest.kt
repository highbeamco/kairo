package io.limberapp.backend.module.auth.endpoint.feature.role

import com.piperframework.testing.responseContent
import io.limberapp.backend.module.auth.api.feature.role.FeatureRoleApi
import io.limberapp.backend.module.auth.api.org.role.OrgRoleApi
import io.limberapp.backend.module.auth.exception.feature.FeatureRoleNotFound
import io.limberapp.backend.module.auth.rep.feature.FeatureRoleRep
import io.limberapp.backend.module.auth.testing.ResourceTest
import io.limberapp.backend.module.auth.testing.fixtures.feature.FeatureRoleRepFixtures
import io.limberapp.backend.module.auth.testing.fixtures.org.OrgRoleRepFixtures
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

internal class DeleteFeatureRoleTest : ResourceTest() {
  @Test
  fun featureRoleDoesNotExist() {
    val featureGuid = UUID.randomUUID()
    val featureRoleGuid = UUID.randomUUID()

    piperTest.test(
      endpoint = FeatureRoleApi.Delete(featureGuid, featureRoleGuid),
      expectedException = FeatureRoleNotFound()
    )
  }

  @Test
  fun happyPath() {
    val featureGuid = UUID.randomUUID()
    val orgGuid = UUID.randomUUID()

    val adminOrgRoleRep = OrgRoleRepFixtures.adminFixture.complete(this, 0)
    piperTest.setup(OrgRoleApi.Post(orgGuid, OrgRoleRepFixtures.adminFixture.creation()))

    val memberOrgRoleRep = OrgRoleRepFixtures.memberFixture.complete(this, 1)
    piperTest.setup(OrgRoleApi.Post(orgGuid, OrgRoleRepFixtures.memberFixture.creation()))

    val featureAdminRoleRep = FeatureRoleRepFixtures.fixture.complete(this, adminOrgRoleRep.guid, 2)
    piperTest.setup(FeatureRoleApi.Post(featureGuid, FeatureRoleRepFixtures.fixture.creation(adminOrgRoleRep.guid)))

    val featureMemberRoleRep = FeatureRoleRepFixtures.fixture.complete(this, memberOrgRoleRep.guid, 3)
    piperTest.setup(FeatureRoleApi.Post(featureGuid, FeatureRoleRepFixtures.fixture.creation(memberOrgRoleRep.guid)))

    piperTest.test(FeatureRoleApi.Delete(featureGuid, featureMemberRoleRep.guid)) {}

    piperTest.test(FeatureRoleApi.GetByFeatureGuid(featureGuid)) {
      val actual = json.parseSet<FeatureRoleRep.Complete>(responseContent)
      assertEquals(setOf(featureAdminRoleRep), actual)
    }
  }
}
