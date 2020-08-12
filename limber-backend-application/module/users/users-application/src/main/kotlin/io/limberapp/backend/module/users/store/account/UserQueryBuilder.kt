package io.limberapp.backend.module.users.store.account

import com.piperframework.store.QueryBuilder
import io.limberapp.backend.module.users.model.account.UserFinder
import java.util.*

internal class UserQueryBuilder : QueryBuilder(), UserFinder {
  override fun orgGuid(orgGuid: UUID) {
    conditions += "org_guid = :orgGuid"
    bindings["orgGuid"] = orgGuid
  }

  override fun userGuid(userGuid: UUID) {
    conditions += "guid = :userGuid"
    bindings["userGuid"] = userGuid
  }

  override fun emailAddress(emailAddress: String) {
    conditions += "LOWER(email_address) = LOWER(:emailAddress)"
    bindings["emailAddress"] = emailAddress
  }
}