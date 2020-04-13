package io.limberapp.backend.module.auth.rep.tenant

import com.piperframework.rep.CompleteRep
import com.piperframework.rep.CreationRep
import com.piperframework.rep.UpdateRep
import com.piperframework.types.LocalDateTime
import com.piperframework.types.UUID
import com.piperframework.validation.RepValidation
import com.piperframework.validation.ifPresent
import com.piperframework.validator.Validator

object TenantRep {

    data class Creation(
        val orgId: UUID,
        val auth0ClientId: String,
        val domain: TenantDomainRep.Creation
    ) : CreationRep {
        override fun validate() = RepValidation {
            validate(Creation::auth0ClientId) { Validator.auth0ClientId(value) }
            validate(Creation::domain)
        }
    }

    data class Complete(
        override val created: LocalDateTime,
        val orgId: UUID,
        val auth0ClientId: String,
        val domains: List<TenantDomainRep.Complete>
    ) : CompleteRep

    data class Update(
        val auth0ClientId: String? = null
    ) : UpdateRep {
        override fun validate() = RepValidation {
            validate(Update::auth0ClientId) { ifPresent { Validator.auth0ClientId(value) } }
        }
    }
}
