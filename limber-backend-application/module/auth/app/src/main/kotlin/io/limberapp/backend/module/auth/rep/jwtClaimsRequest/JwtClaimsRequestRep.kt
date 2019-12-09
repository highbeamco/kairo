package io.limberapp.backend.module.auth.rep.jwtClaimsRequest

import com.fasterxml.jackson.annotation.JsonProperty
import com.piperframework.rep.CreationRep
import com.piperframework.validation.util.emailAddress
import com.piperframework.validation.util.ifPresent
import com.piperframework.validation.util.mediumText
import com.piperframework.validation.util.url
import io.limberapp.backend.authorization.principal.Claims

object JwtClaimsRequestRep {

    data class Creation(
        val firstName: String? = null,
        val lastName: String? = null,
        val emailAddress: String,
        val profilePhotoUrl: String? = null
    ) : CreationRep {
        override fun validate() {
            validate(Creation::firstName) { ifPresent { mediumText(allowEmpty = false) } }
            validate(Creation::lastName) { ifPresent { mediumText(allowEmpty = false) } }
            validate(Creation::emailAddress) { emailAddress() }
            validate(Creation::profilePhotoUrl) { ifPresent { url() } }
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