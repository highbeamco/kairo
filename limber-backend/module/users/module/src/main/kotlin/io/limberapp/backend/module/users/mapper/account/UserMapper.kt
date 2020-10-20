package io.limberapp.backend.module.users.mapper.account

import com.google.inject.Inject
import io.limberapp.backend.module.users.model.account.UserModel
import io.limberapp.backend.module.users.rep.account.UserRep
import io.limberapp.common.util.uuid.UuidGenerator
import io.limberapp.permissions.AccountRole
import java.time.Clock
import java.time.ZonedDateTime

internal class UserMapper @Inject constructor(
    private val clock: Clock,
    private val uuidGenerator: UuidGenerator,
) {
  fun model(rep: UserRep.Creation) = UserModel(
      guid = uuidGenerator.generate(),
      createdDate = ZonedDateTime.now(clock),
      identityProvider = false,
      superuser = false,
      orgGuid = rep.orgGuid,
      firstName = rep.firstName,
      lastName = rep.lastName,
      emailAddress = rep.emailAddress,
      profilePhotoUrl = rep.profilePhotoUrl
  )

  fun summaryRep(model: UserModel) = UserRep.Summary(
      guid = model.guid,
      createdDate = model.createdDate,
      orgGuid = model.orgGuid,
      firstName = model.firstName,
      lastName = model.lastName,
      fullName = model.fullName,
      profilePhotoUrl = model.profilePhotoUrl
  )

  fun completeRep(model: UserModel) = UserRep.Complete(
      guid = model.guid,
      createdDate = model.createdDate,
      roles = AccountRole.values().filter { model.hasRole(it) }.toSet(),
      orgGuid = model.orgGuid,
      firstName = model.firstName,
      lastName = model.lastName,
      fullName = model.fullName,
      emailAddress = model.emailAddress,
      profilePhotoUrl = model.profilePhotoUrl
  )

  fun update(rep: UserRep.Update) = UserModel.Update(
      identityProvider = rep.identityProvider,
      superuser = rep.superuser,
      firstName = rep.firstName,
      lastName = rep.lastName
  )
}
