package kairo.optional

/**
 * Kairo Optionals can be used to differentiate between missing and null values.
 * This comes in especially handy for RFC 7396 (JSON Merge Patch).
 */
public sealed class Optional<out T : Any> : OptionalBase<T>() {
  /** The value was absent from the JSON. Treat as "do not modify". */
  public data object Missing : Optional<Nothing>() {
    override val isSpecified: Boolean = false

    override fun getOrThrow(): Nothing {
      error("Optional value is missing.")
    }
  }

  /** The value was explicitly set to null in the JSON. Treat as "clear this field". */
  public data object Null : Optional<Nothing>() {
    override val isSpecified: Boolean = true

    override fun getOrThrow(): Nothing? =
      null
  }

  /** The value was present and non-null in the JSON. */
  public data class Value<T : Any>(val value: T) : Optional<T>() {
    override val isSpecified: Boolean = true

    override fun getOrThrow(): T =
      value
  }

  public companion object {
    /** Converts a nullable value: null becomes [Null], non-null becomes [Value]. Never returns [Missing]. */
    public fun <T : Any> fromNullable(value: T?): Optional<T> =
      if (value == null) Null else Value(value)
  }
}

/**
 * Runs [block] for [Optional.Null] and [Optional.Value], skips [Optional.Missing].
 * The block receives null for the Null variant.
 */
public fun <T : Any> Optional<T>.ifSpecified(block: (T?) -> Unit) {
  when (this) {
    is Optional.Missing -> Unit
    is Optional.Null -> block(null)
    is Optional.Value -> block(value)
  }
}

/** Applies [block] to the inner value, preserving [Optional.Missing] state. */
public fun <T : Any, R : Any> Optional<T>.transform(block: (T?) -> R?): Optional<R> =
  when (this) {
    is Optional.Missing -> this
    is Optional.Null -> Optional.fromNullable(block(null))
    is Optional.Value -> Optional.fromNullable(block(value))
  }
