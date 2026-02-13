package kairo.rest

/**
 * Annotations for [RestEndpoint] instances.
 */
@Target(AnnotationTarget.CLASS)
public annotation class Rest(
  val method: String,
  val path: String,
) {
  /** Declares the expected request body MIME type (such as "application/json"). */
  @Target(AnnotationTarget.CLASS)
  public annotation class ContentType(val value: String)

  /** Declares the response body MIME type (such as "application/json"). */
  @Target(AnnotationTarget.CLASS)
  public annotation class Accept(val value: String)
}
