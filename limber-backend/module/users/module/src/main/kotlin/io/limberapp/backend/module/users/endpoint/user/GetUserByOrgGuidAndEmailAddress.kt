package io.limberapp.backend.module.users.endpoint.user

import com.google.inject.Inject
import io.ktor.application.ApplicationCall
import io.limberapp.backend.module.users.api.user.UserApi
import io.limberapp.backend.module.users.exception.user.UserNotFound
import io.limberapp.backend.module.users.mapper.user.UserMapper
import io.limberapp.backend.module.users.rep.user.UserRep
import io.limberapp.backend.module.users.service.user.UserService
import io.limberapp.common.auth.auth.AuthOrgMember
import io.limberapp.common.auth.auth.AuthUser
import io.limberapp.common.restInterface.EndpointHandler
import io.limberapp.common.restInterface.template
import java.util.UUID

internal class GetUserByOrgGuidAndEmailAddress @Inject constructor(
    private val userService: UserService,
    private val userMapper: UserMapper,
) : EndpointHandler<UserApi.GetByOrgGuidAndEmailAddress, UserRep.Complete>(
    template = UserApi.GetByOrgGuidAndEmailAddress::class.template(),
) {
  override suspend fun endpoint(call: ApplicationCall): UserApi.GetByOrgGuidAndEmailAddress =
      UserApi.GetByOrgGuidAndEmailAddress(
          orgGuid = call.getParam(UUID::class, "orgGuid"),
          emailAddress = call.getParam(String::class, "emailAddress"),
      )

  override suspend fun Handler.handle(
      endpoint: UserApi.GetByOrgGuidAndEmailAddress,
  ): UserRep.Complete {
    auth { AuthOrgMember(endpoint.orgGuid) }
    val user = userService.getByEmailAddress(endpoint.orgGuid, endpoint.emailAddress)
        ?: throw UserNotFound()
    auth { AuthUser(user.guid) }
    return userMapper.completeRep(user)
  }
}
