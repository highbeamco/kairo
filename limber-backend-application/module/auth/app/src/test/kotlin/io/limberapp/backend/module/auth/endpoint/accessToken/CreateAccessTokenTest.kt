package io.limberapp.backend.module.auth.endpoint.accessToken

import com.fasterxml.jackson.module.kotlin.readValue
import io.limberapp.backend.module.auth.rep.accessToken.AccessTokenRep
import io.limberapp.backend.module.auth.testing.ResourceTest
import io.limberapp.backend.module.auth.testing.fixtures.accessToken.AccessTokenRepFixtures
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals

internal class CreateAccessTokenTest : ResourceTest() {

    @Test
    fun happyPath() {

        // Setup
        val accountId = UUID.randomUUID()

        // CreateAccessToken
        val accessTokenRep = AccessTokenRepFixtures[0].complete(this, accountId, 0)
        val accessTokenOneTimeUseRep = AccessTokenRepFixtures[0].oneTimeUse(this, accountId, 0)
        piperTest.test(
            endpointConfig = CreateAccessToken.endpointConfig,
            pathParams = mapOf(CreateAccessToken.accountId to accountId)
        ) {
            val actual = objectMapper.readValue<AccessTokenRep.OneTimeUse>(response.content!!)
            assertEquals(accessTokenOneTimeUseRep, actual)
        }

        // GetAccessTokensByAccountId
        piperTest.test(
            endpointConfig = GetAccessTokensByAccountId.endpointConfig,
            pathParams = mapOf(GetAccessTokensByAccountId.accountId to accountId)
        ) {
            val actual = objectMapper.readValue<List<AccessTokenRep.Complete>>(response.content!!)
            assertEquals(listOf(accessTokenRep), actual)
        }
    }
}
