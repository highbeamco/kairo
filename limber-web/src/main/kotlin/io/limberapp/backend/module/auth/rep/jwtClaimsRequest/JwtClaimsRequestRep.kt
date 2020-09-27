package io.limberapp.backend.module.auth.rep.jwtClaimsRequest

import io.limberapp.common.rep.CreationRep
import io.limberapp.common.validation.RepValidation
import io.limberapp.common.validation.ifPresent
import io.limberapp.common.validator.Validator
import kotlinx.serialization.Serializable

object JwtClaimsRequestRep {
  @Serializable
  data class Creation(
    val auth0ClientId: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val emailAddress: String,
    val profilePhotoUrl: String? = null,
  ) : CreationRep {
    override fun validate() = RepValidation {
      validate(Creation::auth0ClientId) { Validator.auth0ClientId(value) }
      validate(Creation::firstName) { ifPresent { Validator.humanName(value) } }
      validate(Creation::lastName) { ifPresent { Validator.humanName(value) } }
      validate(Creation::emailAddress) { Validator.emailAddress(value) }
      validate(Creation::profilePhotoUrl) { ifPresent { Validator.url(value) } }
    }
  }

  @Serializable
  data class Complete(
    val org: String?,
    val roles: String,
    val user: String,
  ) : Any()
}