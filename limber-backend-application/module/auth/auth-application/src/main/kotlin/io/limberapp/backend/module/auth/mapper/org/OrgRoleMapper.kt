package io.limberapp.backend.module.auth.mapper.org

import com.google.inject.Inject
import com.piperframework.util.uuid.UuidGenerator
import io.limberapp.backend.authorization.permissions.orgPermissions.OrgPermissions
import io.limberapp.backend.module.auth.model.org.OrgRoleModel
import io.limberapp.backend.module.auth.rep.org.OrgRoleRep
import java.time.Clock
import java.time.LocalDateTime
import java.util.*

internal class OrgRoleMapper @Inject constructor(
  private val clock: Clock,
  private val uuidGenerator: UuidGenerator
) {
  fun model(orgGuid: UUID, rep: OrgRoleRep.Creation) = OrgRoleModel(
    guid = uuidGenerator.generate(),
    createdDate = LocalDateTime.now(clock),
    orgGuid = orgGuid,
    name = rep.name,
    permissions = OrgPermissions.none(),
    memberCount = 0
  )

  fun completeRep(model: OrgRoleModel) = OrgRoleRep.Complete(
    guid = model.guid,
    createdDate = model.createdDate,
    name = model.name,
    permissions = model.permissions,
    memberCount = model.memberCount
  )

  fun update(rep: OrgRoleRep.Update) = OrgRoleModel.Update(
    name = rep.name,
    permissions = rep.permissions
  )
}
