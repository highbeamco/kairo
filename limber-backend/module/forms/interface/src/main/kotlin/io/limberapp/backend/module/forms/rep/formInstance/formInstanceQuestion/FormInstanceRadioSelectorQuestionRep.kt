package io.limberapp.backend.module.forms.rep.formInstance.formInstanceQuestion

import io.limberapp.backend.module.forms.rep.formInstance.FormInstanceQuestionRep
import io.limberapp.common.validator.Validator
import io.limberapp.validation.RepValidation
import java.time.LocalDateTime
import java.util.*

object FormInstanceRadioSelectorQuestionRep {
  data class Creation(
    val selection: String,
  ) : FormInstanceQuestionRep.Creation {
    override fun validate() = RepValidation {
      validate(Creation::selection) { Validator.length1hundred(value, allowEmpty = false) }
    }
  }

  data class Complete(
    override val createdDate: LocalDateTime,
    override val questionGuid: UUID?,
    val selection: String,
  ) : FormInstanceQuestionRep.Complete
}