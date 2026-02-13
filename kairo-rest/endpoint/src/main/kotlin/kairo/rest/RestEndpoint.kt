package kairo.rest

/**
 * A [RestEndpoint] implementation defines the API contract for a single REST API endpoint.
 * Implementations must be Kotlin data classes or data objects.
 * See this Feature's README or tests for some examples.
 *
 * [I] (input) represents the type of the request body. If none, use [Unit].
 * [O] (output) represents the type of the response body. If none, use [Unit].
 */
public abstract class RestEndpoint<I : Any, O : Any> {
  /** Marks a constructor parameter as a path parameter (matches `:paramName` in the path). */
  @Target(AnnotationTarget.VALUE_PARAMETER)
  public annotation class PathParam

  /** Marks a constructor parameter as a query parameter. */
  @Target(AnnotationTarget.VALUE_PARAMETER)
  public annotation class QueryParam

  /**
   * Override in data class endpoints that accept a request body.
   * Throws [NotImplementedError] by default, which is expected for endpoints without a body.
   */
  public open val body: I
    get() {
      throw NotImplementedError()
    }
}
