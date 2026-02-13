package kairo.optional

/**
 * Common base for [Optional] and [Required].
 * Provides [isSpecified] and [getOrThrow] so code can operate on either type generically.
 */
public abstract class OptionalBase<out T : Any> {
  public abstract val isSpecified: Boolean

  public abstract fun getOrThrow(): T?
}
