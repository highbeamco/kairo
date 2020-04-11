package io.limberapp.backend.module.users.rep.account

import com.piperframework.rep.CompleteRep
import com.piperframework.rep.CreationRep
import com.piperframework.rep.UpdateRep
import com.piperframework.types.LocalDateTime
import com.piperframework.types.UUID
import com.piperframework.validation.RepValidation
import com.piperframework.validation.ifPresent
import com.piperframework.validator.Validator
import io.limberapp.backend.authorization.principal.JwtRole

object UserRep {

    data class Creation(
        val orgId: UUID,
        val firstName: String,
        val lastName: String,
        val emailAddress: String,
        val profilePhotoUrl: String? = null
    ) : CreationRep {
        override fun validate() = RepValidation {
            validate(Creation::firstName) { Validator.humanName(value) }
            validate(Creation::lastName) { Validator.humanName(value) }
            validate(Creation::emailAddress) { Validator.emailAddress(value) }
            validate(Creation::profilePhotoUrl) { ifPresent { Validator.url(value) } }
        }
    }

    data class Complete(
        val id: UUID,
        override val created: LocalDateTime,
        val orgId: UUID,
        val firstName: String,
        val lastName: String,
        val emailAddress: String,
        val profilePhotoUrl: String?,
        val roles: Set<JwtRole>
    ) : CompleteRep

    data class Update(
        val firstName: String? = null,
        val lastName: String? = null
    ) : UpdateRep {
        override fun validate() = RepValidation {
            validate(Update::firstName) { ifPresent { Validator.humanName(value) } }
            validate(Update::lastName) { ifPresent { Validator.humanName(value) } }
        }
    }
}