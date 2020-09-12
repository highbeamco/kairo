@file:Suppress("UNREACHABLE_CODE")

package io.limberapp.backend.adhoc

import com.google.inject.Injector
import com.piperframework.shutDown
import io.ktor.application.Application
import io.limberapp.backend.BaseLimberApp
import io.limberapp.backend.LimberModule
import io.limberapp.backend.config.LimberConfigLoader
import io.limberapp.backend.module.LimberSqlModule
import io.limberapp.backend.module.auth.model.tenant.TenantDomainModel
import io.limberapp.backend.module.auth.model.tenant.TenantModel
import io.limberapp.backend.module.auth.service.tenant.TenantDomainService
import io.limberapp.backend.module.auth.service.tenant.TenantService
import io.limberapp.backend.module.orgs.model.org.OrgModel
import io.limberapp.backend.module.orgs.service.org.OrgService
import java.time.LocalDateTime
import java.util.*

private object OnboardArgs {
  val orgName: String get() = TODO()
  val auth0ClientId: String get() = TODO()
  val orgDomain: String get() = TODO()
}

internal fun Adhoc.onboard() {
  val config = LimberConfigLoader().load()

  object : BaseLimberApp(application, config) {
    override fun getMainModules(application: Application) =
      super.getMainModules(application) + LimberSqlModule(config.sqlDatabase, runMigrations = false)

    override val modules = allLimberModules()

    override fun Application.afterStart(context: Context) {
      val orgGuid = createOrg(context.injector)
      createTenant(context.injector, orgGuid = orgGuid)
      createTenantDomain(context.injector, orgGuid = orgGuid)
      shutDown(0)
    }

    @OptIn(LimberModule.Orgs::class)
    private fun createOrg(injector: Injector): UUID {
      val orgService = injector.getInstance(OrgService::class.java)
      return orgService.create(OrgModel(
        guid = UUID.randomUUID(),
        createdDate = LocalDateTime.now(),
        name = OnboardArgs.orgName,
        ownerUserGuid = null,
      )).guid
    }

    @OptIn(LimberModule.Auth::class)
    private fun createTenant(injector: Injector, orgGuid: UUID) {
      val tenantService = injector.getInstance(TenantService::class.java)
      tenantService.create(TenantModel(
        createdDate = LocalDateTime.now(),
        orgGuid = orgGuid,
        auth0ClientId = OnboardArgs.auth0ClientId,
      ))
    }

    @OptIn(LimberModule.Auth::class)
    private fun createTenantDomain(injector: Injector, orgGuid: UUID) {
      val tenantDomainService = injector.getInstance(TenantDomainService::class.java)
      tenantDomainService.create(TenantDomainModel(
        createdDate = LocalDateTime.now(),
        orgGuid = orgGuid,
        domain = OnboardArgs.orgDomain,
      ))
    }
  }
}
