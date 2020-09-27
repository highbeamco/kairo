package io.limberapp.backend.module.users.endpoint.user.role

import com.google.inject.Inject
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.limberapp.backend.authorization.Authorization
import io.limberapp.backend.authorization.principal.JwtRole
import io.limberapp.backend.endpoint.LimberApiEndpoint
import io.limberapp.backend.module.users.api.user.role.UserRoleApi
import io.limberapp.backend.module.users.model.account.UserModel
import io.limberapp.backend.module.users.service.account.UserService
import io.limberapp.common.restInterface.template
import java.util.*

internal class PutUserRole @Inject constructor(
  application: Application,
  private val userService: UserService,
) : LimberApiEndpoint<UserRoleApi.Put, Unit>(
  application = application,
  endpointTemplate = UserRoleApi.Put::class.template()
) {
  override suspend fun determineCommand(call: ApplicationCall) = UserRoleApi.Put(
    userGuid = call.parameters.getAsType(UUID::class, "userGuid"),
    role = call.parameters.getAsType(JwtRole::class, "role")
  )

  override suspend fun Handler.handle(command: UserRoleApi.Put) {
    Authorization.Role(JwtRole.SUPERUSER).authorize()
    userService.update(command.userGuid, UserModel.Update.fromRole(command.role, true))
  }
}