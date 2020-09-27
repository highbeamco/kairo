package io.limberapp.backend.module.auth.api.tenant.domain

import io.limberapp.backend.module.auth.rep.tenant.TenantDomainRep
import io.limberapp.common.restInterface.HttpMethod
import io.limberapp.common.restInterface.LimberEndpoint
import io.limberapp.common.util.url.enc

@Suppress("StringLiteralDuplication")
object TenantDomainApi {
  data class Post(val orgGuid: String, val rep: TenantDomainRep.Creation?) : LimberEndpoint(
    httpMethod = HttpMethod.POST,
    path = "/tenants/${enc(orgGuid)}/domains",
    body = rep
  )

  data class Delete(val orgGuid: String, val domain: String) : LimberEndpoint(
    httpMethod = HttpMethod.DELETE,
    path = "/tenants/${enc(orgGuid)}/domains/${enc(domain)}"
  )
}