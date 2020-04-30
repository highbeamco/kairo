package io.limberapp.backend.module.auth.endpoint.org.role

import io.limberapp.backend.module.auth.api.org.role.OrgRoleApi
import io.limberapp.backend.module.auth.exception.org.OrgRoleNotFound
import io.limberapp.backend.module.auth.rep.org.OrgRoleRep
import io.limberapp.backend.module.auth.testing.ResourceTest
import io.limberapp.backend.module.auth.testing.fixtures.org.OrgRoleRepFixtures
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals

internal class DeleteOrgRoleTest : ResourceTest() {
    @Test
    fun orgDoesNotExist() {
        val orgGuid = UUID.randomUUID()
        val orgRoleGuid = UUID.randomUUID()

        piperTest.test(
            endpoint = OrgRoleApi.Delete(orgGuid, orgRoleGuid),
            expectedException = OrgRoleNotFound()
        )
    }

    @Test
    fun orgRoleDoesNotExist() {
        val orgGuid = UUID.randomUUID()
        val orgRoleGuid = UUID.randomUUID()

        piperTest.test(
            endpoint = OrgRoleApi.Delete(orgGuid, orgRoleGuid),
            expectedException = OrgRoleNotFound()
        )
    }

    @Test
    fun happyPath() {
        val orgGuid = UUID.randomUUID()

        val adminOrgRoleRep = OrgRoleRepFixtures.adminFixture.complete(this, 0)
        piperTest.setup(OrgRoleApi.Post(orgGuid, OrgRoleRepFixtures.adminFixture.creation()))

        val memberOrgRoleRep = OrgRoleRepFixtures.adminFixture.complete(this, 1)
        piperTest.setup(OrgRoleApi.Post(orgGuid, OrgRoleRepFixtures.memberFixture.creation()))

        piperTest.test(OrgRoleApi.Delete(orgGuid, memberOrgRoleRep.guid)) {}

        piperTest.test(OrgRoleApi.GetByOrgGuid(orgGuid)) {
            val actual = json.parseSet<OrgRoleRep.Complete>(response.content!!)
            assertEquals(setOf(adminOrgRoleRep), actual)
        }
    }
}
