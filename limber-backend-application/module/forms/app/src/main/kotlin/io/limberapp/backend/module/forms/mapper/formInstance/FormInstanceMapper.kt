package io.limberapp.backend.module.forms.mapper.formInstance

import com.google.inject.Inject
import com.piperframework.util.uuid.uuidGenerator.UuidGenerator
import io.limberapp.backend.module.forms.model.formInstance.FormInstanceModel
import io.limberapp.backend.module.forms.rep.formInstance.FormInstanceRep
import java.time.Clock
import java.time.LocalDateTime

internal class FormInstanceMapper @Inject constructor(
    private val clock: Clock,
    private val uuidGenerator: UuidGenerator,
    private val formInstanceQuestionMapper: FormInstanceQuestionMapper
) {

    fun model(rep: FormInstanceRep.Creation) = FormInstanceModel(
        id = uuidGenerator.generate(),
        created = LocalDateTime.now(clock),
        orgId = rep.orgId,
        formTemplateId = rep.formTemplateId,
        questions = rep.questions.map { formInstanceQuestionMapper.model(it) }
    )

    fun completeRep(model: FormInstanceModel) = FormInstanceRep.Complete(
        id = model.id,
        created = model.created,
        orgId = model.orgId,
        formTemplateId = model.formTemplateId,
        questions = model.questions.map { formInstanceQuestionMapper.completeRep(it) }
    )
}
