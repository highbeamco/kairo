package io.limberapp.backend.module.orgs.endpoint.org.role

import io.limberapp.backend.module.orgs.api.org.OrgApi
import io.limberapp.backend.module.orgs.api.org.role.OrgRoleApi
import io.limberapp.backend.module.orgs.exception.org.OrgNotFound
import io.limberapp.backend.module.orgs.exception.org.OrgRoleIsNotUnique
import io.limberapp.backend.module.orgs.rep.org.OrgRoleRep
import io.limberapp.backend.module.orgs.testing.ResourceTest
import io.limberapp.backend.module.orgs.testing.fixtures.org.OrgRepFixtures
import io.limberapp.backend.module.orgs.testing.fixtures.orgRole.OrgRoleRepFixtures
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals

internal class PostOrgRoleTest : ResourceTest() {
    @Test
    fun orgDoesNotExist() {
        val orgGuid = UUID.randomUUID()

        piperTest.test(
            endpoint = OrgRoleApi.Post(orgGuid, OrgRoleRepFixtures.memberFixture.creation()),
            expectedException = OrgNotFound()
        )
    }

    @Test
    fun duplicateName() {
        val orgOwnerAccountGuid = UUID.randomUUID()

        val orgRep = OrgRepFixtures.crankyPastaFixture.complete(this, orgOwnerAccountGuid, 0)
        piperTest.setup(OrgApi.Post(OrgRepFixtures.crankyPastaFixture.creation(orgOwnerAccountGuid)))

        val adminOrgRoleRep = OrgRoleRepFixtures.adminFixture.complete(this, 2)
        piperTest.setup(OrgRoleApi.Post(orgRep.guid, OrgRoleRepFixtures.adminFixture.creation()))

        piperTest.test(
            endpoint = OrgRoleApi.Post(
                orgGuid = orgRep.guid,
                rep = OrgRoleRepFixtures.memberFixture.creation().copy(name = adminOrgRoleRep.name)
            ),
            expectedException = OrgRoleIsNotUnique()
        )

        piperTest.test(OrgRoleApi.GetByOrgGuid(orgRep.guid)) {
            val actual = json.parseSet<OrgRoleRep.Complete>(response.content!!)
            assertEquals(setOf(adminOrgRoleRep), actual)
        }
    }

    @Test
    fun happyPath() {
        val orgOwnerAccountGuid = UUID.randomUUID()

        val orgRep = OrgRepFixtures.crankyPastaFixture.complete(this, orgOwnerAccountGuid, 0)
        piperTest.setup(OrgApi.Post(OrgRepFixtures.crankyPastaFixture.creation(orgOwnerAccountGuid)))

        val orgRoleRep = OrgRoleRepFixtures.adminFixture.complete(this, 2)
        piperTest.test(OrgRoleApi.Post(orgRep.guid, OrgRoleRepFixtures.adminFixture.creation())) {
            val actual = json.parse<OrgRoleRep.Complete>(response.content!!)
            assertEquals(orgRoleRep, actual)
        }

        piperTest.test(OrgRoleApi.GetByOrgGuid(orgRep.guid)) {
            val actual = json.parseSet<OrgRoleRep.Complete>(response.content!!)
            assertEquals(setOf(orgRoleRep), actual)
        }
    }
}
