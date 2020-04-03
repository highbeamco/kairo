package io.limberapp.backend.module.users.endpoint.user

import io.limberapp.backend.module.users.exception.account.EmailAddressAlreadyTaken
import io.limberapp.backend.module.users.rep.account.UserRep
import io.limberapp.backend.module.users.testing.ResourceTest
import io.limberapp.backend.module.users.testing.fixtures.user.UserRepFixtures
import kotlinx.serialization.parse
import kotlinx.serialization.stringify
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals

internal class PostUserTest : ResourceTest() {

    @Test
    fun duplicateEmailAddress() {

        // Setup
        val orgId = UUID.randomUUID()

        // PostUser
        val jeffHudsonUserRep = UserRepFixtures.jeffHudsonFixture.complete(this, orgId, 0)
        piperTest.setup(
            endpointConfig = PostUser.endpointConfig,
            body = json.stringify(UserRepFixtures.jeffHudsonFixture.creation(orgId))
        )

        // PostUser
        piperTest.test(
            endpointConfig = PostUser.endpointConfig,
            body = json.stringify(
                UserRepFixtures.billGatesFixture.creation(orgId).copy(emailAddress = jeffHudsonUserRep.emailAddress)
            ),
            expectedException = EmailAddressAlreadyTaken(jeffHudsonUserRep.emailAddress)
        )
    }

    @Test
    fun happyPath() {

        // Setup
        val orgId = UUID.randomUUID()

        // PostUser
        val userRep = UserRepFixtures.jeffHudsonFixture.complete(this, orgId, 0)
        piperTest.test(
            endpointConfig = PostUser.endpointConfig,
            body = json.stringify(UserRepFixtures.jeffHudsonFixture.creation(orgId))
        ) {
            val actual = json.parse<UserRep.Complete>(response.content!!)
            assertEquals(userRep, actual)
        }

        // GetUser
        piperTest.test(
            endpointConfig = GetUser.endpointConfig,
            pathParams = mapOf(GetUser.userId to userRep.id)
        ) {
            val actual = json.parse<UserRep.Complete>(response.content!!)
            assertEquals(userRep, actual)
        }
    }
}
