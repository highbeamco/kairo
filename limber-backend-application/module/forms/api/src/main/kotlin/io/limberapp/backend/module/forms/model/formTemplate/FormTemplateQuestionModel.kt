package io.limberapp.backend.module.forms.model.formTemplate

import java.time.LocalDateTime
import java.util.UUID

interface FormTemplateQuestionModel {

    val id: UUID
    val created: LocalDateTime
    val label: String
    val helpText: String?
    val type: Type

    enum class Type {
        DATE,
        TEXT;
    }

    interface Update {
        val label: String?
        val helpText: String?
    }
}