package io.limberapp.backend.module.auth.endpoint.org.role

import com.google.inject.Inject
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.limberapp.backend.authorization.authorization.AuthFeatureMember
import io.limberapp.backend.endpoint.LimberApiEndpoint
import io.limberapp.backend.module.auth.api.org.OrgRoleApi
import io.limberapp.backend.module.auth.mapper.org.OrgRoleMapper
import io.limberapp.backend.module.auth.rep.org.OrgRoleRep
import io.limberapp.backend.module.auth.service.org.OrgRoleService
import io.limberapp.common.permissions.orgPermissions.OrgPermission
import io.limberapp.common.restInterface.template
import java.util.*

internal class PatchOrgRole @Inject constructor(
    application: Application,
    private val orgRoleService: OrgRoleService,
    private val orgRoleMapper: OrgRoleMapper,
) : LimberApiEndpoint<OrgRoleApi.Patch, OrgRoleRep.Complete>(
    application = application,
    endpointTemplate = OrgRoleApi.Patch::class.template()
) {
  override suspend fun determineCommand(call: ApplicationCall) = OrgRoleApi.Patch(
      orgGuid = call.parameters.getAsType(UUID::class, "orgGuid"),
      orgRoleGuid = call.parameters.getAsType(UUID::class, "orgRoleGuid"),
      rep = call.getAndValidateBody()
  )

  override suspend fun Handler.handle(command: OrgRoleApi.Patch): OrgRoleRep.Complete {
    val rep = command.rep.required()
    auth { AuthFeatureMember(command.orgGuid, permission = OrgPermission.MANAGE_ORG_ROLES) }
    val orgRole = orgRoleService.update(command.orgGuid, command.orgRoleGuid, orgRoleMapper.update(rep))
    return orgRoleMapper.completeRep(orgRole)
  }
}
