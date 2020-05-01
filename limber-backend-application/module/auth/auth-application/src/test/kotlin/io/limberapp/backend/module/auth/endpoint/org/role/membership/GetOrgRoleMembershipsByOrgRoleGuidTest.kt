package io.limberapp.backend.module.auth.endpoint.org.role.membership

import io.limberapp.backend.module.auth.api.org.role.OrgRoleApi
import io.limberapp.backend.module.auth.api.org.role.OrgRoleMembershipApi
import io.limberapp.backend.module.auth.exception.org.OrgRoleNotFound
import io.limberapp.backend.module.auth.rep.org.OrgRoleMembershipRep
import io.limberapp.backend.module.auth.testing.ResourceTest
import io.limberapp.backend.module.auth.testing.fixtures.org.OrgRoleMembershipRepFixtures
import io.limberapp.backend.module.auth.testing.fixtures.org.OrgRoleRepFixtures
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class GetOrgRoleMembershipsByOrgRoleGuidTest : ResourceTest() {
    @Test
    fun orgRoleDoesNotExist() {
        val orgGuid = UUID.randomUUID()
        val orgRoleGuid = UUID.randomUUID()

        // Create an org role anyways, to ensure that the error still happens when there is one.
        piperTest.setup(OrgRoleApi.Post(orgGuid, OrgRoleRepFixtures.adminFixture.creation()))

        piperTest.test(
            endpoint = OrgRoleMembershipApi.GetByOrgRoleGuid(orgGuid, orgRoleGuid),
            expectedException = OrgRoleNotFound()
        )
    }

    @Test
    fun happyPathNone() {
        val orgGuid = UUID.randomUUID()

        val orgRoleRep = OrgRoleRepFixtures.adminFixture.complete(this, 0)
        piperTest.setup(OrgRoleApi.Post(orgGuid, OrgRoleRepFixtures.adminFixture.creation()))

        piperTest.test(OrgRoleMembershipApi.GetByOrgRoleGuid(orgGuid, orgRoleRep.guid)) {
            val actual = json.parseSet<OrgRoleMembershipRep.Complete>(response.content!!)
            assertTrue(actual.isEmpty())
        }
    }

    @Test
    fun happyPathTwo() {
        val orgGuid = UUID.randomUUID()
        val account0Guid = UUID.randomUUID()
        val account1Guid = UUID.randomUUID()

        val orgRoleRep = OrgRoleRepFixtures.adminFixture.complete(this, 0)
        piperTest.setup(OrgRoleApi.Post(orgGuid, OrgRoleRepFixtures.adminFixture.creation()))

        val orgRoleMembershipRep0 = OrgRoleMembershipRepFixtures.fixture.complete(this, orgRoleRep.guid, account0Guid)
        piperTest.setup(
            endpoint = OrgRoleMembershipApi.Post(
                orgGuid = orgGuid,
                orgRoleGuid = orgRoleRep.guid,
                rep = OrgRoleMembershipRepFixtures.fixture.creation(account0Guid)
            )
        )

        val orgRoleMembershipRep1 = OrgRoleMembershipRepFixtures.fixture.complete(this, orgRoleRep.guid, account1Guid)
        piperTest.setup(
            endpoint = OrgRoleMembershipApi.Post(
                orgGuid = orgGuid,
                orgRoleGuid = orgRoleRep.guid,
                rep = OrgRoleMembershipRepFixtures.fixture.creation(account1Guid)
            )
        )

        piperTest.test(OrgRoleMembershipApi.GetByOrgRoleGuid(orgGuid, orgRoleRep.guid)) {
            val actual = json.parseSet<OrgRoleMembershipRep.Complete>(response.content!!)
            assertEquals(setOf(orgRoleMembershipRep0, orgRoleMembershipRep1), actual)
        }
    }
}