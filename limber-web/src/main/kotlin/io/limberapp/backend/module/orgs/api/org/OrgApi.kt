package io.limberapp.backend.module.orgs.api.org

import io.limberapp.backend.module.orgs.rep.org.OrgRep
import io.limberapp.common.restInterface.HttpMethod
import io.limberapp.common.restInterface.LimberEndpoint
import io.limberapp.common.util.url.enc

@Suppress("StringLiteralDuplication")
object OrgApi {
  data class Post(val rep: OrgRep.Creation?) : LimberEndpoint(
    httpMethod = HttpMethod.POST,
    path = "/orgs",
    body = rep
  )

  data class Get(val orgGuid: String) : LimberEndpoint(
    httpMethod = HttpMethod.GET,
    path = "/orgs/${enc(orgGuid)}"
  )

  data class GetByOwnerUserGuid(val ownerUserGuid: String) : LimberEndpoint(
    httpMethod = HttpMethod.GET,
    path = "/orgs",
    queryParams = listOf("ownerUserGuid" to enc(ownerUserGuid))
  )

  data class Patch(val orgGuid: String, val rep: OrgRep.Update?) : LimberEndpoint(
    httpMethod = HttpMethod.PATCH,
    path = "/orgs/${enc(orgGuid)}",
    body = rep
  )

  data class Delete(val orgGuid: String) : LimberEndpoint(
    httpMethod = HttpMethod.DELETE,
    path = "/orgs/${enc(orgGuid)}"
  )
}
