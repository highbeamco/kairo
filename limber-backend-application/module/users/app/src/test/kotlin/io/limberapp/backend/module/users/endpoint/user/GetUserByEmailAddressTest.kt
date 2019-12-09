package io.limberapp.backend.module.users.endpoint.user

import com.fasterxml.jackson.module.kotlin.readValue
import io.limberapp.backend.module.users.exception.notFound.UserNotFound
import io.limberapp.backend.module.users.rep.user.UserRep
import io.limberapp.backend.module.users.testing.ResourceTest
import io.limberapp.backend.module.users.testing.fixtures.user.UserRepFixtures
import org.junit.Test
import kotlin.test.assertEquals

internal class GetUserByEmailAddressTest : ResourceTest() {

    @Test
    fun doesNotExist() {

        // Setup
        val emailAddress = "jhudson@jhudson.ca"

        // GetUserByEmailAddress
        piperTest.test(
            endpointConfig = GetUserByEmailAddress.endpointConfig,
            queryParams = mapOf(GetUserByEmailAddress.emailAddress to emailAddress),
            expectedException = UserNotFound()
        )
    }

    @Test
    fun happyPath() {

        // CreateUser
        val userRep = UserRepFixtures[0].complete(this, 0)
        piperTest.test(
            endpointConfig = CreateUser.endpointConfig,
            body = UserRepFixtures[0].creation()
        ) {}

        // GetUserByEmailAddress
        piperTest.test(
            endpointConfig = GetUserByEmailAddress.endpointConfig,
            queryParams = mapOf(GetUserByEmailAddress.emailAddress to userRep.emailAddress)
        ) {
            val actual = objectMapper.readValue<UserRep.Complete>(response.content!!)
            assertEquals(userRep, actual)
        }
    }
}