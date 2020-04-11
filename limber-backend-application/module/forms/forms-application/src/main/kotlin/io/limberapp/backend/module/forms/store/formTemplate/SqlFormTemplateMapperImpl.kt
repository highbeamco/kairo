package io.limberapp.backend.module.forms.store.formTemplate

import com.google.inject.Inject
import com.piperframework.util.unknown
import io.limberapp.backend.module.forms.entity.formTemplate.FormTemplateQuestionTable
import io.limberapp.backend.module.forms.entity.formTemplate.FormTemplateTable
import io.limberapp.backend.module.forms.model.formTemplate.FormTemplateModel
import io.limberapp.backend.module.forms.model.formTemplate.FormTemplateQuestionModel
import io.limberapp.backend.module.forms.model.formTemplate.formTemplateQuestion.FormTemplateDateQuestionModel
import io.limberapp.backend.module.forms.model.formTemplate.formTemplateQuestion.FormTemplateTextQuestionModel
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.statements.UpdateStatement
import java.util.UUID
import kotlin.reflect.KClass

internal class SqlFormTemplateMapperImpl @Inject constructor(
    private val formTemplateQuestionStore: FormTemplateQuestionStore
) : SqlFormTemplateMapper {

    override fun formTemplateEntity(insertStatement: InsertStatement<*>, model: FormTemplateModel) {
        insertStatement[FormTemplateTable.createdDate] = model.created
        insertStatement[FormTemplateTable.guid] = model.id
        insertStatement[FormTemplateTable.featureGuid] = model.featureId
        insertStatement[FormTemplateTable.title] = model.title
        insertStatement[FormTemplateTable.description] = model.description
    }

    override fun formTemplateEntity(updateStatement: UpdateStatement, update: FormTemplateModel.Update) {
        update.title?.let { updateStatement[FormTemplateTable.title] = it }
        update.description?.let { updateStatement[FormTemplateTable.description] = it }
    }

    override fun formTemplateQuestionEntity(
        insertStatement: InsertStatement<*>,
        formTemplateId: UUID,
        model: FormTemplateQuestionModel,
        rank: Int
    ) {
        insertStatement[FormTemplateQuestionTable.createdDate] = model.created
        insertStatement[FormTemplateQuestionTable.guid] = model.id
        insertStatement[FormTemplateQuestionTable.formTemplateGuid] = formTemplateId
        insertStatement[FormTemplateQuestionTable.rank] = rank
        insertStatement[FormTemplateQuestionTable.label] = model.label
        insertStatement[FormTemplateQuestionTable.helpText] = model.helpText
        insertStatement[FormTemplateQuestionTable.type] = model.type.name
        when (model) {
            is FormTemplateDateQuestionModel -> {
                insertStatement[FormTemplateQuestionTable.earliest] = model.earliest
                insertStatement[FormTemplateQuestionTable.latest] = model.latest
            }
            is FormTemplateTextQuestionModel -> {
                insertStatement[FormTemplateQuestionTable.multiLine] = model.multiLine
                insertStatement[FormTemplateQuestionTable.placeholder] = model.placeholder
                insertStatement[FormTemplateQuestionTable.validator] = model.validator?.pattern
            }
            else -> unknownFormTemplateQuestion(model::class)
        }
    }

    override fun formTemplateQuestionEntity(
        updateStatement: UpdateStatement,
        update: FormTemplateQuestionModel.Update
    ) {
        update.label?.let { updateStatement[FormTemplateQuestionTable.label] = it }
        update.helpText?.let { updateStatement[FormTemplateQuestionTable.helpText] = it }
        when (update) {
            is FormTemplateDateQuestionModel.Update -> {
                update.earliest?.let { updateStatement[FormTemplateQuestionTable.earliest] = it }
                update.latest?.let { updateStatement[FormTemplateQuestionTable.latest] = it }
            }
            is FormTemplateTextQuestionModel.Update -> {
                update.multiLine?.let { updateStatement[FormTemplateQuestionTable.multiLine] = it }
                update.placeholder?.let { updateStatement[FormTemplateQuestionTable.placeholder] = it }
                update.validator?.let { updateStatement[FormTemplateQuestionTable.validator] = it.pattern }
            }
            else -> unknownFormTemplateQuestion(update::class)
        }
    }

    override fun formTemplateModel(resultRow: ResultRow): FormTemplateModel {
        val guid = resultRow[FormTemplateTable.guid]
        return FormTemplateModel(
            id = guid,
            created = resultRow[FormTemplateTable.createdDate],
            featureId = resultRow[FormTemplateTable.featureGuid],
            title = resultRow[FormTemplateTable.title],
            description = resultRow[FormTemplateTable.description],
            questions = formTemplateQuestionStore.getByFormTemplateId(guid)
        )
    }

    override fun formTemplateQuestionModel(resultRow: ResultRow) =
        when (FormTemplateQuestionModel.Type.valueOf(resultRow[FormTemplateQuestionTable.type])) {
            FormTemplateQuestionModel.Type.DATE -> FormTemplateDateQuestionModel(
                id = resultRow[FormTemplateQuestionTable.guid],
                created = resultRow[FormTemplateQuestionTable.createdDate],
                label = resultRow[FormTemplateQuestionTable.label],
                helpText = resultRow[FormTemplateQuestionTable.helpText],
                earliest = resultRow[FormTemplateQuestionTable.earliest],
                latest = resultRow[FormTemplateQuestionTable.latest]
            )
            FormTemplateQuestionModel.Type.TEXT -> FormTemplateTextQuestionModel(
                id = resultRow[FormTemplateQuestionTable.guid],
                created = resultRow[FormTemplateQuestionTable.createdDate],
                label = resultRow[FormTemplateQuestionTable.label],
                helpText = resultRow[FormTemplateQuestionTable.helpText],
                multiLine = checkNotNull(resultRow[FormTemplateQuestionTable.multiLine]),
                placeholder = resultRow[FormTemplateQuestionTable.placeholder],
                validator = resultRow[FormTemplateQuestionTable.validator]?.let { Regex(it) }
            )
        }

    private fun unknownFormTemplateQuestion(klass: KClass<*>) {
        unknown("form template question", klass::class)
    }
}