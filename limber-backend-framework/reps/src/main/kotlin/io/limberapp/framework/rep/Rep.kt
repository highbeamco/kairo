package io.limberapp.framework.rep

import io.limberapp.framework.validation.Validation
import java.time.LocalDateTime
import java.util.UUID
import kotlin.reflect.KProperty1

sealed class ValidatedRep {

    abstract fun validate()

    fun <R : ValidatedRep, T : Any?> R.validate(
        property: KProperty1<R, T>,
        validator: Validation<T>.() -> Unit
    ) {
        Validation(property.get(this), property.name).apply(validator)
    }
}

abstract class CreationSubrep : ValidatedRep()

abstract class CreationRep : ValidatedRep()

abstract class CompleteSubrep {
    abstract val created: LocalDateTime
}

abstract class CompleteRep {
    abstract val id: UUID
    abstract val created: LocalDateTime
}

abstract class UpdateSubrep : ValidatedRep()

abstract class UpdateRep : ValidatedRep()
