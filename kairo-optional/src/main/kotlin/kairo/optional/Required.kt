package kairo.optional

/**
 * Like [Optional] but without a Null variant.
 * Use when the value must be non-null if present.
 * Useful for required fields in JSON Merge Patch.
 */
public sealed class Required<out T : Any> : OptionalBase<T>() {
  abstract override fun getOrThrow(): T

  /** The value was absent from the JSON. Treat as "do not modify". */
  public data object Missing : Required<Nothing>() {
    override val isSpecified: Boolean = false

    override fun getOrThrow(): Nothing {
      error("Required value is missing.")
    }
  }

  /** The value was present and non-null in the JSON. */
  public data class Value<T : Any>(val value: T) : Required<T>() {
    override val isSpecified: Boolean = true

    override fun getOrThrow(): T =
      value
  }

  public companion object {
    /** Wraps a non-null value in [Value]. */
    public fun <T : Any> of(value: T): Required<T> =
      Value(value)
  }
}

/** Runs [block] for [Required.Value], skips [Required.Missing]. */
public fun <T : Any> Required<T>.ifSpecified(block: (T) -> Unit) {
  when (this) {
    is Required.Missing -> Unit
    is Required.Value -> block(value)
  }
}

/** Applies [block] to the inner value, preserving [Required.Missing] state. */
public fun <T : Any, R : Any> Required<T>.transform(block: (T) -> R): Required<R> =
  when (this) {
    is Required.Missing -> this
    is Required.Value -> Required.of(block(value))
  }
