package io.limberapp.backend.module.auth.exception.feature

import io.limberapp.common.exception.exception.conflict.ConflictException

internal class FeatureRoleOrgRoleIsNotUnique : ConflictException(
  message = "The feature role already exists.",
  developerMessage = "This exception should be thrown when an attempt is made to create a feature role and there is" +
    " already a feature role for that feature with the same org role."
)