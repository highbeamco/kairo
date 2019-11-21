package io.limberapp.backend.module.users.store.user

import io.limberapp.backend.module.users.entity.user.UserEntity
import io.limberapp.framework.endpoint.authorization.jwt.JwtRole
import io.limberapp.framework.store.CrudStore
import java.util.UUID

internal interface UserStore : CrudStore<UserEntity, UserEntity.Update> {

    fun getByEmailAddress(emailAddress: String): UserEntity?

    fun addRole(userId: UUID, roleName: JwtRole): Unit?

    fun removeRole(userId: UUID, roleName: JwtRole): Unit?
}
