package io.limberapp.backend.module.auth.endpoint.feature.role

import io.limberapp.backend.module.auth.api.feature.role.FeatureRoleApi
import io.limberapp.backend.module.auth.api.org.role.OrgRoleApi
import io.limberapp.backend.module.auth.exception.feature.FeatureRoleOrgRoleIsNotUnique
import io.limberapp.backend.module.auth.rep.feature.FeatureRoleRep
import io.limberapp.backend.module.auth.testing.ResourceTest
import io.limberapp.backend.module.auth.testing.fixtures.feature.FeatureRoleRepFixtures
import io.limberapp.backend.module.auth.testing.fixtures.org.OrgRoleRepFixtures
import io.limberapp.common.testing.responseContent
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

internal class PostFeatureRoleTest : ResourceTest() {
  @Test
  fun duplicateFeatureRoleGuid() {
    val featureGuid = UUID.randomUUID()

    val adminOrgRoleRep = OrgRoleRepFixtures.adminFixture.complete(this, 0)
    limberTest.setup(OrgRoleApi.Post(adminOrgRoleRep.guid, OrgRoleRepFixtures.adminFixture.creation()))

    val featureAdminOrgRoleRep = FeatureRoleRepFixtures.fixture.complete(this, adminOrgRoleRep.guid, 1)
    limberTest.setup(FeatureRoleApi.Post(featureGuid, FeatureRoleRepFixtures.fixture.creation(adminOrgRoleRep.guid)))

    limberTest.test(
      endpoint = FeatureRoleApi.Post(
        featureGuid = featureGuid,
        rep = FeatureRoleRepFixtures.fixture.creation(adminOrgRoleRep.guid)
      ),
      expectedException = FeatureRoleOrgRoleIsNotUnique()
    )

    limberTest.test(FeatureRoleApi.GetByFeatureGuid(featureGuid)) {
      val actual = json.parseSet<FeatureRoleRep.Complete>(responseContent)
      assertEquals(setOf(featureAdminOrgRoleRep), actual)
    }
  }

  @Test
  fun happyPath() {
    val orgGuid = UUID.randomUUID()
    val featureGuid = UUID.randomUUID()

    val adminOrgRoleRep = OrgRoleRepFixtures.adminFixture.complete(this, 0)
    limberTest.setup(OrgRoleApi.Post(orgGuid, OrgRoleRepFixtures.adminFixture.creation()))

    val featureAdminRoleRep = FeatureRoleRepFixtures.fixture.complete(this, adminOrgRoleRep.guid, 1)
    limberTest.test(FeatureRoleApi.Post(featureGuid, FeatureRoleRepFixtures.fixture.creation(adminOrgRoleRep.guid))) {
      val actual = json.parse<FeatureRoleRep.Complete>(responseContent)
      assertEquals(featureAdminRoleRep, actual)
    }

    limberTest.test(FeatureRoleApi.GetByFeatureGuid(featureGuid)) {
      val actual = json.parseSet<FeatureRoleRep.Complete>(responseContent)
      assertEquals(setOf(featureAdminRoleRep), actual)
    }
  }
}