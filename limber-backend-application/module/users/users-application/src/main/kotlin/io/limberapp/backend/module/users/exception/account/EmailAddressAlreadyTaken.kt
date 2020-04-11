package io.limberapp.backend.module.users.exception.account

import com.piperframework.exception.exception.conflict.ConflictException

internal class EmailAddressAlreadyTaken(emailAddress: String) : ConflictException(
    message = "The email address \"$emailAddress\" is already taken.",
    developerMessage = "This exception should be thrown when an attempt is made to set a user's email address to an" +
            " email address that is already taken by an existing user."
)