package kairo.coroutines

import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll

/** Emits all elements from the [collection] into this flow. Convenience overload of [emitAll] for [Iterable]. */
public suspend fun <T> FlowCollector<T>.emitAll(collection: Iterable<T>) {
  emitAll(collection.asFlow())
}
