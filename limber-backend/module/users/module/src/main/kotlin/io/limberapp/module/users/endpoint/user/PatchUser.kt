package io.limberapp.module.users.endpoint.user

import com.google.inject.Inject
import io.ktor.application.ApplicationCall
import io.limberapp.auth.Auth
import io.limberapp.auth.auth.AuthLimberPermission
import io.limberapp.auth.auth.AuthOrgMember
import io.limberapp.auth.auth.AuthUser
import io.limberapp.module.users.api.user.UserApi
import io.limberapp.module.users.mapper.user.UserMapper
import io.limberapp.module.users.rep.user.UserRep
import io.limberapp.module.users.service.user.UserService
import io.limberapp.permissions.limber.LimberPermission
import io.limberapp.permissions.org.OrgPermission
import io.limberapp.restInterface.EndpointHandler
import io.limberapp.restInterface.template
import java.util.UUID

internal class PatchUser @Inject constructor(
    private val userService: UserService,
    private val userMapper: UserMapper,
) : EndpointHandler<UserApi.Patch, UserRep.Complete>(
    template = UserApi.Patch::class.template(),
) {
  override suspend fun endpoint(call: ApplicationCall): UserApi.Patch =
      UserApi.Patch(
          userGuid = call.getParam(UUID::class, "userGuid"),
          rep = call.getAndValidateBody(),
      )

  override suspend fun Handler.handle(endpoint: UserApi.Patch): UserRep.Complete {
    val rep = endpoint.rep.required()
    auth {
      Auth.All(
          AuthUser(endpoint.userGuid),
          AuthOrgMember(null, permission = OrgPermission.MODIFY_OWN_METADATA),
          if (rep.permissions != null) AuthLimberPermission(LimberPermission.SUPERUSER)
          else Auth.Allow,
      )
    }
    val user = userService.update(endpoint.userGuid, userMapper.update(rep))
    return userMapper.completeRep(user)
  }
}