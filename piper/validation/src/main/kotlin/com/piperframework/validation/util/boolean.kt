package com.piperframework.validation.util

import com.piperframework.validation.Validation
import com.piperframework.validation.exception.ValidationException

fun Validation<String>.boolean(boolean: Boolean) {
    if (!boolean) throw ValidationException(name)
}