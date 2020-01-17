package io.limberapp.backend.module.auth.testing.fixtures.accessToken

import com.piperframework.util.uuid.base64Encode
import io.limberapp.backend.module.auth.rep.accessToken.AccessTokenRep
import io.limberapp.backend.module.auth.testing.ResourceTest
import java.time.LocalDateTime
import java.util.UUID

internal object AccessTokenRepFixtures {

    data class Fixture(
        val oneTimeUse: ResourceTest.(userId: UUID, idSeed: Int) -> AccessTokenRep.OneTimeUse,
        val complete: ResourceTest.(userId: UUID, idSeed: Int) -> AccessTokenRep.Complete
    )

    operator fun get(i: Int) = fixtures[i]

    private val fixtures = listOf(
        Fixture({ userId, idSeed ->
            AccessTokenRep.OneTimeUse(
                id = deterministicUuidGenerator[idSeed],
                created = LocalDateTime.now(fixedClock),
                userId = userId,
                token = deterministicUuidGenerator[idSeed + 1].base64Encode()
            )
        }, { userId, idSeed ->
            AccessTokenRep.Complete(
                id = deterministicUuidGenerator[idSeed],
                created = LocalDateTime.now(fixedClock),
                userId = userId
            )
        })
    )
}