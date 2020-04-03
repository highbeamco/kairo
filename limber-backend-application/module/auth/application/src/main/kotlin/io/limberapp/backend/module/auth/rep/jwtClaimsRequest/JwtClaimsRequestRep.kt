package io.limberapp.backend.module.auth.rep.jwtClaimsRequest

import com.fasterxml.jackson.annotation.JsonProperty
import com.piperframework.rep.CreationRep
import com.piperframework.validation.RepValidation
import com.piperframework.validation.ifPresent
import com.piperframework.validator.Validator
import io.limberapp.backend.authorization.principal.Claims

internal object JwtClaimsRequestRep {

    data class Creation(
        val auth0ClientId: String,
        val firstName: String,
        val lastName: String,
        val emailAddress: String,
        val profilePhotoUrl: String? = null
    ) : CreationRep {
        override fun validate() = RepValidation {
            validate(Creation::auth0ClientId) { Validator.auth0ClientId(value) }
            validate(Creation::firstName) { Validator.humanName(value) }
            validate(Creation::lastName) { Validator.humanName(value) }
            validate(Creation::emailAddress) { Validator.emailAddress(value) }
            validate(Creation::profilePhotoUrl) { ifPresent { Validator.url(value) } }
        }
    }

    data class Complete(
        @JsonProperty(Claims.org)
        val org: String,
        @JsonProperty(Claims.roles)
        val roles: String,
        @JsonProperty(Claims.user)
        val user: String
    ) : Any()
}
