package io.limberapp.backend.module.users.endpoint.user.role

import io.ktor.server.testing.TestApplicationEngine
import io.limberapp.backend.authorization.principal.JwtRole
import io.limberapp.backend.module.users.api.user.UserApi
import io.limberapp.backend.module.users.api.user.role.UserRoleApi
import io.limberapp.backend.module.users.exception.account.UserNotFound
import io.limberapp.backend.module.users.rep.account.UserRep
import io.limberapp.backend.module.users.testing.IntegrationTest
import io.limberapp.backend.module.users.testing.fixtures.account.UserRepFixtures
import io.limberapp.common.LimberApplication
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

internal class PutUserRoleTest(
  engine: TestApplicationEngine,
  limberServer: LimberApplication<*>,
) : IntegrationTest(engine, limberServer) {
  @Test
  fun userDoesNotExist() {
    val userGuid = UUID.randomUUID()

    test(
      endpoint = UserRoleApi.Put(userGuid, JwtRole.SUPERUSER),
      expectedException = UserNotFound()
    )
  }

  @Test
  fun happyPath() {
    val orgGuid = UUID.randomUUID()

    var userRep = UserRepFixtures.jeffHudsonFixture.complete(this, orgGuid, 0)
    setup(UserApi.Post(UserRepFixtures.jeffHudsonFixture.creation(orgGuid)))

    userRep = userRep.copy(roles = userRep.roles + JwtRole.SUPERUSER)
    test(UserRoleApi.Put(userRep.guid, JwtRole.SUPERUSER)) {}

    test(UserApi.Get(userRep.guid)) {
      val actual = json.parse<UserRep.Complete>(responseContent)
      assertEquals(userRep, actual)
    }
  }

  @Test
  fun happyPathIdempotent() {
    val orgGuid = UUID.randomUUID()

    var userRep = UserRepFixtures.jeffHudsonFixture.complete(this, orgGuid, 0)
    setup(UserApi.Post(UserRepFixtures.jeffHudsonFixture.creation(orgGuid)))

    userRep = userRep.copy(roles = userRep.roles + JwtRole.SUPERUSER)
    setup(UserRoleApi.Put(userRep.guid, JwtRole.SUPERUSER))

    test(UserRoleApi.Put(userRep.guid, JwtRole.SUPERUSER)) {}

    test(UserApi.Get(userRep.guid)) {
      val actual = json.parse<UserRep.Complete>(responseContent)
      assertEquals(userRep, actual)
    }
  }
}
