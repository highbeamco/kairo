package io.limberapp.backend.module.forms.store.formTemplate

import com.google.inject.Inject
import com.piperframework.exception.exception.badRequest.RankOutOfBounds
import com.piperframework.store.SqlStore
import com.piperframework.util.singleNullOrThrow
import io.limberapp.backend.module.forms.entity.formTemplate.FormTemplateQuestionEntity
import io.limberapp.backend.module.forms.exception.formTemplate.FormTemplateQuestionNotFound
import io.limberapp.backend.module.forms.model.formTemplate.FormTemplateQuestionModel
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.bindKotlin
import java.util.UUID

internal class FormTemplateQuestionStore @Inject constructor(private val jdbi: Jdbi) : SqlStore() {
    fun create(formTemplateGuid: UUID, models: List<FormTemplateQuestionModel>, rank: Int? = null) {
        jdbi.useTransaction<Exception> {
            val insertionRank = validateInsertionRank(formTemplateGuid, rank)
            incrementExistingRanks(formTemplateGuid, atLeast = insertionRank, incrementBy = models.size)
            it.prepareBatch(sqlResource("create")).apply {
                models.forEachIndexed { i, model ->
                    this
                        .bind("formTemplateGuid", formTemplateGuid)
                        .bind("rank", insertionRank + i)
                        .bindKotlin(FormTemplateQuestionEntity(model))
                        .add()
                }
            }.execute()
        }
    }

    fun create(formTemplateGuid: UUID, model: FormTemplateQuestionModel, rank: Int? = null) {
        jdbi.useTransaction<Exception> {
            val insertionRank = validateInsertionRank(formTemplateGuid, rank)
            incrementExistingRanks(formTemplateGuid, atLeast = insertionRank, incrementBy = 1)
            it.createUpdate(sqlResource("create"))
                .bind("formTemplateGuid", formTemplateGuid)
                .bind("rank", insertionRank)
                .bindKotlin(FormTemplateQuestionEntity(model))
                .execute()
        }
    }

    private fun validateInsertionRank(formTemplateGuid: UUID, rank: Int?): Int {
        rank?.let { if (it < 0) throw RankOutOfBounds(it) }
        val maxExistingRank = jdbi.withHandle<Int, Exception> {
            it.createQuery(sqlResource("getMaxRank"))
                .bind("formTemplateGuid", formTemplateGuid)
                .asInt()
        } ?: -1
        rank?.let { if (it > maxExistingRank + 1) throw RankOutOfBounds(it) }
        return rank ?: maxExistingRank + 1
    }

    private fun incrementExistingRanks(formTemplateGuid: UUID, atLeast: Int, incrementBy: Int) {
        jdbi.useHandle<Exception> {
            it.createUpdate(sqlResource("incrementExistingRanks"))
                .bind("formTemplateGuid", formTemplateGuid)
                .bind("atLeast", atLeast)
                .bind("incrementBy", incrementBy)
                .execute()
        }
    }

    fun get(formTemplateGuid: UUID, questionGuid: UUID): FormTemplateQuestionModel? {
        return jdbi.withHandle<FormTemplateQuestionModel?, Exception> {
            it.createQuery(sqlResource("get"))
                .bind("formTemplateGuid", formTemplateGuid)
                .bind("guid", questionGuid)
                .mapTo(FormTemplateQuestionEntity::class.java)
                .singleNullOrThrow()
                ?.asModel()
        }
    }

    fun getByFormTemplateGuid(formTemplateGuid: UUID): List<FormTemplateQuestionModel> {
        return jdbi.withHandle<List<FormTemplateQuestionModel>, Exception> {
            it.createQuery(sqlResource("getByFormTemplateGuid"))
                .bind("formTemplateGuid", formTemplateGuid)
                .mapTo(FormTemplateQuestionEntity::class.java)
                .map { it.asModel() }
                .toList()
        }
    }

    fun update(
        formTemplateGuid: UUID,
        questionGuid: UUID,
        update: FormTemplateQuestionModel.Update
    ): FormTemplateQuestionModel {
        return jdbi.inTransaction<FormTemplateQuestionModel, Exception> {
            val updateCount = it.createUpdate(sqlResource("update"))
                .bind("formTemplateGuid", formTemplateGuid)
                .bind("questionGuid", questionGuid)
                .bindKotlin(FormTemplateQuestionEntity.Update(update))
                .execute()
            when (updateCount) {
                0 -> throw FormTemplateQuestionNotFound()
                1 -> return@inTransaction checkNotNull(get(formTemplateGuid, questionGuid))
                else -> badSql()
            }
        }
    }

    fun delete(formTemplateGuid: UUID, questionGuid: UUID) {
        jdbi.useTransaction<Exception> {
            val updateCount = it.createUpdate(sqlResource("delete"))
                .bind("formTemplateGuid", formTemplateGuid)
                .bind("questionGuid", questionGuid)
                .execute()
            when (updateCount) {
                0 -> throw FormTemplateQuestionNotFound()
                1 -> return@useTransaction
                else -> badSql()
            }
        }
    }
}
