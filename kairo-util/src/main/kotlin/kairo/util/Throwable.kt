package kairo.util

/** Walks the exception cause chain and returns the first cause matching type [T], or null if none match. */
public inline fun <reified T> Throwable.firstCauseOf(): T? =
  generateSequence(this) { it.cause }
    .filterIsInstance<T>()
    .firstOrNull()
