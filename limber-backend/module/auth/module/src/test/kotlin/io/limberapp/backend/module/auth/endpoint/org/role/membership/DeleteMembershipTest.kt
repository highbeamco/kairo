package io.limberapp.backend.module.auth.endpoint.org.role.membership

import io.ktor.server.testing.TestApplicationEngine
import io.limberapp.backend.module.auth.api.org.role.OrgRoleApi
import io.limberapp.backend.module.auth.api.org.role.OrgRoleMembershipApi
import io.limberapp.backend.module.auth.exception.org.OrgRoleMembershipNotFound
import io.limberapp.backend.module.auth.rep.org.OrgRoleMembershipRep
import io.limberapp.backend.module.auth.testing.IntegrationTest
import io.limberapp.backend.module.auth.testing.fixtures.org.OrgRoleMembershipRepFixtures
import io.limberapp.backend.module.auth.testing.fixtures.org.OrgRoleRepFixtures
import io.limberapp.common.LimberApplication
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

internal class DeleteMembershipTest(
  engine: TestApplicationEngine,
  limberServer: LimberApplication<*>,
) : IntegrationTest(engine, limberServer) {
  @Test
  fun orgRoleDoesNotExist() {
    val orgGuid = UUID.randomUUID()
    val orgRoleGuid = UUID.randomUUID()
    val accountGuid = UUID.randomUUID()

    // Create an org role anyways, to ensure that the error still happens when there is one.
    setup(OrgRoleApi.Post(orgGuid, OrgRoleRepFixtures.adminFixture.creation()))

    test(
      endpoint = OrgRoleMembershipApi.Delete(orgGuid, orgRoleGuid, accountGuid),
      expectedException = OrgRoleMembershipNotFound()
    )
  }

  @Test
  fun orgRoleMembershipDoesNotExist() {
    val orgGuid = UUID.randomUUID()
    val orgRoleGuid = UUID.randomUUID()
    val account0Guid = UUID.randomUUID()
    val account1Guid = UUID.randomUUID()

    val orgRoleRep = OrgRoleRepFixtures.adminFixture.complete(this, 0)
    setup(OrgRoleApi.Post(orgGuid, OrgRoleRepFixtures.adminFixture.creation()))

    // Create an org role membership anyways, to ensure that the error still happens when there is one.
    setup(
      endpoint = OrgRoleMembershipApi.Post(
        orgGuid = orgGuid,
        orgRoleGuid = orgRoleRep.guid,
        rep = OrgRoleMembershipRepFixtures.fixture.creation(account0Guid)
      )
    )

    test(
      endpoint = OrgRoleMembershipApi.Delete(orgGuid, orgRoleGuid, account1Guid),
      expectedException = OrgRoleMembershipNotFound()
    )
  }

  @Test
  fun happyPath() {
    val orgGuid = UUID.randomUUID()
    val account0Guid = UUID.randomUUID()
    val account1Guid = UUID.randomUUID()

    val orgRoleRep = OrgRoleRepFixtures.adminFixture.complete(this, 0)
    setup(OrgRoleApi.Post(orgGuid, OrgRoleRepFixtures.adminFixture.creation()))

    setup(
      endpoint = OrgRoleMembershipApi.Post(
        orgGuid = orgGuid,
        orgRoleGuid = orgRoleRep.guid,
        rep = OrgRoleMembershipRepFixtures.fixture.creation(account0Guid)
      )
    )

    val orgRoleMembershipRep1 = OrgRoleMembershipRepFixtures.fixture.complete(this, account1Guid)
    setup(
      endpoint = OrgRoleMembershipApi.Post(
        orgGuid = orgGuid,
        orgRoleGuid = orgRoleRep.guid,
        rep = OrgRoleMembershipRepFixtures.fixture.creation(account1Guid)
      )
    )

    test(OrgRoleMembershipApi.Delete(orgGuid, orgRoleRep.guid, account0Guid)) {}

    test(OrgRoleMembershipApi.GetByOrgRoleGuid(orgGuid, orgRoleRep.guid)) {
      val actual = json.parseSet<OrgRoleMembershipRep.Complete>(responseContent)
      assertEquals(setOf(orgRoleMembershipRep1), actual)
    }
  }
}
