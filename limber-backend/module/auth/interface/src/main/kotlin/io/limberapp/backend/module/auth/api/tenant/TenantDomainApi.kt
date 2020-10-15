package io.limberapp.backend.module.auth.api.tenant

import io.ktor.http.HttpMethod
import io.limberapp.backend.module.auth.rep.tenant.TenantDomainRep
import io.limberapp.common.restInterface.LimberEndpoint
import io.limberapp.util.url.enc
import java.util.*

@Suppress("StringLiteralDuplication")
object TenantDomainApi {
  data class Post(val orgGuid: UUID, val rep: TenantDomainRep.Creation?) : LimberEndpoint(
      httpMethod = HttpMethod.Post,
      path = "/tenants/${enc(orgGuid)}/domains",
      body = rep
  )

  data class Delete(val orgGuid: UUID, val domain: String) : LimberEndpoint(
      httpMethod = HttpMethod.Delete,
      path = "/tenants/${enc(orgGuid)}/domains/${enc(domain)}"
  )
}