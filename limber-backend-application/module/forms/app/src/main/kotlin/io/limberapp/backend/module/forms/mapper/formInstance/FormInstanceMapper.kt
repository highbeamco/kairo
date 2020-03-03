package io.limberapp.backend.module.forms.mapper.formInstance

import com.google.inject.Inject
import com.piperframework.util.uuid.uuidGenerator.UuidGenerator
import io.limberapp.backend.module.forms.model.formInstance.FormInstanceModel
import io.limberapp.backend.module.forms.model.formInstance.FormInstanceQuestionModel
import io.limberapp.backend.module.forms.model.formInstance.formInstanceQuestion.FormInstanceDateQuestionModel
import io.limberapp.backend.module.forms.model.formInstance.formInstanceQuestion.FormInstanceTextQuestionModel
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
        questions = emptyList()
    )

    fun completeRep(model: FormInstanceModel) = FormInstanceRep.Complete(
        id = model.id,
        created = model.created,
        orgId = model.orgId,
        formTemplateId = model.formTemplateId,
        questions = model.questions.map { formInstanceQuestionMapper.completeRep(it) }
    )

    fun update(model: FormInstanceQuestionModel) = when (model) {
        is FormInstanceDateQuestionModel -> FormInstanceDateQuestionModel.Update(date = model.date)
        is FormInstanceTextQuestionModel -> FormInstanceTextQuestionModel.Update(text = model.text)
        else -> error("Unexpected question type: ${model::class.qualifiedName}")
    }
}