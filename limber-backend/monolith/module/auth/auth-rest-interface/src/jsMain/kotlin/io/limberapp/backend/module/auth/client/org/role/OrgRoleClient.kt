package io.limberapp.backend.module.auth.client.org.role

import io.limberapp.backend.module.auth.api.org.role.OrgRoleApi
import io.limberapp.backend.module.auth.rep.org.OrgRoleRep
import io.limberapp.common.util.Outcome

interface OrgRoleClient {
  suspend operator fun invoke(endpoint: OrgRoleApi.Post): Outcome<OrgRoleRep.Complete>

  suspend operator fun invoke(endpoint: OrgRoleApi.GetByOrgGuid): Outcome<Set<OrgRoleRep.Complete>>

  suspend operator fun invoke(endpoint: OrgRoleApi.Patch): Outcome<OrgRoleRep.Complete>

  suspend operator fun invoke(endpoint: OrgRoleApi.Delete): Outcome<Unit>
}
