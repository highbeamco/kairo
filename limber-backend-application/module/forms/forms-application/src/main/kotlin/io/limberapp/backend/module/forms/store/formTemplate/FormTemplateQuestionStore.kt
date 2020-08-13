package io.limberapp.backend.module.forms.store.formTemplate

import com.google.inject.Inject
import com.google.inject.Singleton
import com.piperframework.exception.exception.badRequest.RankOutOfBounds
import com.piperframework.sql.PolymorphicRowMapper
import com.piperframework.sql.bindNullForMissingArguments
import com.piperframework.store.SqlStore
import io.limberapp.backend.module.forms.exception.formTemplate.FormTemplateQuestionNotFound
import io.limberapp.backend.module.forms.model.formTemplate.FormTemplateQuestionModel
import io.limberapp.backend.module.forms.model.formTemplate.formTemplateQuestion.FormTemplateDateQuestionModel
import io.limberapp.backend.module.forms.model.formTemplate.formTemplateQuestion.FormTemplateRadioSelectorQuestionModel
import io.limberapp.backend.module.forms.model.formTemplate.formTemplateQuestion.FormTemplateTextQuestionModel
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.bindKotlin
import java.util.*

private class FormTemplateQuestionRowMapper : PolymorphicRowMapper<FormTemplateQuestionModel>("type") {
  override fun getClass(type: String) = when (FormTemplateQuestionModel.Type.valueOf(type)) {
    FormTemplateQuestionModel.Type.DATE -> FormTemplateDateQuestionModel::class.java
    FormTemplateQuestionModel.Type.RADIO_SELECTOR -> FormTemplateRadioSelectorQuestionModel::class.java
    FormTemplateQuestionModel.Type.TEXT -> FormTemplateTextQuestionModel::class.java
  }
}

@Singleton
internal class FormTemplateQuestionStore @Inject constructor(private val jdbi: Jdbi) : SqlStore(jdbi) {
  init {
    jdbi.registerRowMapper(FormTemplateQuestionRowMapper())
  }

  fun create(models: List<FormTemplateQuestionModel>, rank: Int? = null) {
    // Batch creation is only supported for questions in the same form template.
    val formTemplateGuid = models.map { it.formTemplateGuid }.distinct().singleOrNull() ?: return
    jdbi.useTransaction<Exception> {
      val insertionRank = validateInsertionRank(formTemplateGuid, rank)
      incrementExistingRanks(formTemplateGuid, atLeast = insertionRank, incrementBy = models.size)
      it.prepareBatch(sqlResource("/store/formTemplateQuestion/create.sql")).apply {
        models.forEachIndexed { i, model ->
          this
            .bind("rank", insertionRank + i)
            .bindKotlin(model)
            .bindNullForMissingArguments()
            .add()
        }
      }.execute()
    }
  }

  fun create(model: FormTemplateQuestionModel, rank: Int? = null): FormTemplateQuestionModel {
    return jdbi.inTransaction<FormTemplateQuestionModel, Exception> {
      val insertionRank = validateInsertionRank(model.formTemplateGuid, rank)
      incrementExistingRanks(model.formTemplateGuid, atLeast = insertionRank, incrementBy = 1)
      it.createQuery(sqlResource("/store/formTemplateQuestion/create.sql"))
        .bind("rank", insertionRank)
        .bindKotlin(model)
        .bindNullForMissingArguments()
        .mapTo(FormTemplateQuestionModel::class.java)
        .single()
    }
  }

  private fun validateInsertionRank(formTemplateGuid: UUID, rank: Int?): Int {
    rank?.let { if (it < 0) throw RankOutOfBounds(it) }
    val maxExistingRank = jdbi.withHandle<Int, Exception> {
      it.createQuery(
        """
        SELECT MAX(rank)
        FROM forms.form_template_question
        WHERE form_template_guid = :formTemplateGuid
        """.trimIndent()
      )
        .bind("formTemplateGuid", formTemplateGuid)
        .asInt()
    } ?: -1
    rank?.let { if (it > maxExistingRank + 1) throw RankOutOfBounds(it) }
    return rank ?: maxExistingRank + 1
  }

  private fun incrementExistingRanks(formTemplateGuid: UUID, atLeast: Int, incrementBy: Int) {
    jdbi.useHandle<Exception> {
      it.createUpdate(
        """
        UPDATE forms.form_template_question
        SET rank = rank + :incrementBy
        WHERE form_template_guid = :formTemplateGuid
          AND rank >= :atLeast
        """.trimIndent()
      )
        .bind("formTemplateGuid", formTemplateGuid)
        .bind("atLeast", atLeast)
        .bind("incrementBy", incrementBy)
        .execute()
    }
  }

  fun get(formTemplateGuid: UUID? = null, questionGuid: UUID? = null): List<FormTemplateQuestionModel> {
    return jdbi.withHandle<List<FormTemplateQuestionModel>, Exception> {
      it.createQuery("SELECT * FROM forms.form_template_question WHERE <conditions> ORDER BY rank").build {
        if (formTemplateGuid != null) {
          conditions.add("form_template_guid = :formTemplateGuid")
          bindings["formTemplateGuid"] = formTemplateGuid
        }
        if (questionGuid != null) {
          conditions.add("guid = :questionGuid")
          bindings["questionGuid"] = questionGuid
        }
      }
        .mapTo(FormTemplateQuestionModel::class.java)
        .list()
    }
  }

  fun update(questionGuid: UUID, update: FormTemplateQuestionModel.Update): FormTemplateQuestionModel =
    inTransaction { handle ->
      val updateCount = handle.createUpdate(sqlResource("/store/formTemplateQuestion/update.sql"))
        .bind("questionGuid", questionGuid)
        .bindKotlin(update)
        .bindNullForMissingArguments()
        .execute()
      return@inTransaction when (updateCount) {
        0 -> throw FormTemplateQuestionNotFound()
        1 -> get(questionGuid = questionGuid).single()
        else -> badSql()
      }
    }

  fun delete(questionGuid: UUID) {
    jdbi.useTransaction<Exception> {
      val updateCount = it.createUpdate(
        """
        DELETE
        FROM forms.form_template_question
        WHERE guid = :questionGuid
        """.trimIndent()
      )
        .bind("questionGuid", questionGuid)
        .execute()
      return@useTransaction when (updateCount) {
        0 -> throw FormTemplateQuestionNotFound()
        1 -> Unit
        else -> badSql()
      }
    }
  }
}
