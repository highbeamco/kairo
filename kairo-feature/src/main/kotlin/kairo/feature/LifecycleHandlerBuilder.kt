package kairo.feature

/** Builder for a single lifecycle handler. Define [start] and [stop] blocks. */
public class LifecycleHandlerBuilder internal constructor() {
  internal var start: (suspend (features: List<Feature>) -> Unit)? = null
  internal var stop: (suspend (features: List<Feature>) -> Unit)? = null

  /** Registers the start block. Receives the full list of Features in the Server. */
  public fun start(block: suspend (features: List<Feature>) -> Unit) {
    require(start == null)
    start = block
  }

  /** Registers the stop block. Receives the full list of Features in the Server. */
  public fun stop(block: suspend (features: List<Feature>) -> Unit) {
    require(stop == null)
    stop = block
  }
}
