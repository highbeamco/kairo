package io.limberapp.backend.module.auth.service.org

import io.limberapp.backend.module.auth.model.org.OrgRoleModel
import java.util.*

interface OrgRoleService {
  fun create(model: OrgRoleModel): OrgRoleModel

  fun getByAccountGuid(orgGuid: UUID, accountGuid: UUID): Set<OrgRoleModel>

  fun getByOrgGuid(orgGuid: UUID): Set<OrgRoleModel>

  fun update(orgGuid: UUID, orgRoleGuid: UUID, update: OrgRoleModel.Update): OrgRoleModel

  fun delete(orgGuid: UUID, orgRoleGuid: UUID)
}