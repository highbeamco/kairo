package io.limberapp.backend.module.forms.rep.formInstance

import com.piperframework.rep.CompleteRep
import com.piperframework.rep.CreationRep
import com.piperframework.types.LocalDateTime
import com.piperframework.types.UUID
import com.piperframework.validation.RepValidation

object FormInstanceRep {

    data class Creation(
        val featureId: UUID,
        val formTemplateId: UUID
    ) : CreationRep {
        override fun validate() = RepValidation {}
    }

    data class Complete(
        val id: UUID,
        override val created: LocalDateTime,
        val featureId: UUID,
        val formTemplateId: UUID,
        val questions: List<FormInstanceQuestionRep.Complete>
    ) : CompleteRep
}