package kairo.rest.exception

internal class UnrecognizedProperty(
  override val path: String?,
  override val location: Location?,
  cause: Exception,
) : JsonBadRequestException(
  message = "Unrecognized property." +
    " This property is not recognized. Is it named incorrectly?",
  cause = cause,
)