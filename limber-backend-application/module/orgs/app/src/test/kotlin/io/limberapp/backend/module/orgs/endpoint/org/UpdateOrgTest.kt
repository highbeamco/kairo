package io.limberapp.backend.module.orgs.endpoint.org

import com.fasterxml.jackson.module.kotlin.readValue
import io.limberapp.backend.module.orgs.exception.notFound.OrgNotFound
import io.limberapp.backend.module.orgs.rep.org.OrgRep
import io.limberapp.backend.module.orgs.testing.ResourceTest
import io.limberapp.backend.module.orgs.testing.fixtures.org.OrgRepFixtures
import org.junit.Test
import java.util.UUID
import kotlin.test.assertEquals

internal class UpdateOrgTest : ResourceTest() {

    @Test
    fun doesNotExist() {

        // Setup
        val orgId = UUID.randomUUID()

        // UpdateOrg
        val orgUpdateRep = OrgRep.Update("Standing Teeth")
        piperTest.test(
            endpointConfig = UpdateOrg.endpointConfig,
            pathParams = mapOf(UpdateOrg.orgId to orgId),
            body = orgUpdateRep,
            expectedException = OrgNotFound()
        )
    }

    @Test
    fun happyPath() {

        // CreateOrg
        var orgRep = OrgRepFixtures[0].complete(this, 0)
        piperTest.setup(
            endpointConfig = CreateOrg.endpointConfig,
            body = OrgRepFixtures[0].creation()
        )

        // UpdateOrg
        val orgUpdateRep = OrgRep.Update("Standing Teeth")
        orgRep = orgRep.copy(name = orgUpdateRep.name!!)
        piperTest.test(
            endpointConfig = UpdateOrg.endpointConfig,
            pathParams = mapOf(UpdateOrg.orgId to orgRep.id),
            body = orgUpdateRep
        ) {
            val actual = objectMapper.readValue<OrgRep.Complete>(response.content!!)
            assertEquals(orgRep, actual)
        }

        // GetOrg
        piperTest.test(
            endpointConfig = GetOrg.endpointConfig,
            pathParams = mapOf(UpdateOrg.orgId to orgRep.id)
        ) {
            val actual = objectMapper.readValue<OrgRep.Complete>(response.content!!)
            assertEquals(orgRep, actual)
        }
    }
}