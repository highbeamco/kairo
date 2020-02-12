package io.limberapp.backend.module.users.service.account

import com.google.inject.Inject
import io.limberapp.backend.module.users.store.account.UserStore

internal class UserServiceImpl @Inject constructor(
    userStore: UserStore
) : UserService by userStore
