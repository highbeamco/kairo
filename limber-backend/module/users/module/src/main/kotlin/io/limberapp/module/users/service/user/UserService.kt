package io.limberapp.module.users.service.user

import io.limberapp.module.users.model.user.UserModel
import java.util.UUID

interface UserService {
  fun create(model: UserModel): UserModel

  operator fun get(userGuid: UUID): UserModel?

  fun getByEmailAddress(orgGuid: UUID, emailAddress: String): UserModel?

  fun getByOrgGuid(orgGuid: UUID): Set<UserModel>

  fun update(userGuid: UUID, update: UserModel.Update): UserModel

  suspend fun delete(userGuid: UUID)
}