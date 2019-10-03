package io.limberapp.framework.validation.validation.util

import io.limberapp.framework.validation.validation.Validation
import io.limberapp.framework.validation.validation.ValidationError

private const val MIN_CHARS = 3
private const val SHORT_TEXT_MAX_CHARS = 100
private const val LONG_TEXT_MAX_CHARS = 10_000

fun Validation<String>.longText(allowEmpty: Boolean) {
    length(IntRange(start = if (allowEmpty) 0 else MIN_CHARS, endInclusive = SHORT_TEXT_MAX_CHARS))
}

fun Validation<String>.shortText(allowEmpty: Boolean) {
    length(IntRange(start = if (allowEmpty) 0 else MIN_CHARS, endInclusive = LONG_TEXT_MAX_CHARS))
}

private fun Validation<String>.length(range: IntRange) {
    if (subject.length !in range) throw ValidationError(
        name
    )
}
