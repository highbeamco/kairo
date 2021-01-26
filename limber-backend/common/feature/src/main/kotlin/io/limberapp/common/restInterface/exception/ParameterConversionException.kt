package io.limberapp.common.restInterface.exception

import io.limberapp.common.exception.badRequest.BadRequestException

class ParameterConversionException(
    parameterName: String,
    cause: Exception? = null,
) : BadRequestException(
    message = "Could not convert parameter to the appropriate type.",
    userVisibleProperties = mapOf("parameterName" to parameterName),
    cause = cause,
)
