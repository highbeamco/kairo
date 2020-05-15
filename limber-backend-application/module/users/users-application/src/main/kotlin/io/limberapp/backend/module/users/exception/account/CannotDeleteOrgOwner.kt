package io.limberapp.backend.module.users.exception.account

import com.piperframework.exception.exception.conflict.ConflictException

internal class CannotDeleteOrgOwner : ConflictException(
  message = "You can't delete your account because you're the owner of your organization." +
    " Delete the organizations instead, or transfer it to another owner.",
  developerMessage = "This exception should be thrown when an attempt is made to delete a user who is the owner of" +
    "the org."
)
