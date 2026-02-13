package kairo.feature

/** Builder DSL for defining lifecycle handlers. Use [handler] to add handlers at specific priorities. */
public class LifecycleBuilder internal constructor() {
  internal val handlers: List<LifecycleHandler>
    field: MutableList<LifecycleHandler> = mutableListOf()

  /**
   * Adds a lifecycle handler at the given [priority].
   * Lower priority values start first and stop last. Defaults to [FeaturePriority.default].
   */
  public fun handler(
    priority: Int = FeaturePriority.default,
    block: LifecycleHandlerBuilder.() -> Unit,
  ) {
    val builder = LifecycleHandlerBuilder().apply(block)
    handlers += LifecycleHandler(
      priority = priority,
      handleStart = builder.start,
      handleStop = builder.stop,
    )
  }
}
