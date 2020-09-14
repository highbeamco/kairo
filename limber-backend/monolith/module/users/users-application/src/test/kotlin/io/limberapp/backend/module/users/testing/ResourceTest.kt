package io.limberapp.backend.module.users.testing

import io.limberapp.backend.module.TestSqlModule
import io.limberapp.backend.module.orgs.service.org.OrgService
import io.limberapp.backend.module.users.UsersModule
import io.limberapp.backend.test.LimberResourceTest
import io.limberapp.common.testing.MockedServices

abstract class ResourceTest : LimberResourceTest() {
  override val module = UsersModule()

  private val testSqlModule = TestSqlModule(config.sqlDatabase)

  protected val mockedServices: MockedServices = MockedServices(OrgService::class)

  override val additionalModules = setOf(mockedServices, testSqlModule)

  override fun before() {
    testSqlModule.dropDatabase()
  }

  override fun after() {
    testSqlModule.unconfigure()
  }
}
