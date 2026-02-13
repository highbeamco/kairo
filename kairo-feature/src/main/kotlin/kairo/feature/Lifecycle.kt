package kairo.feature

/** DSL entry point for defining lifecycle handlers within a Feature. */
public fun lifecycle(block: LifecycleBuilder.() -> Unit): List<LifecycleHandler> =
  LifecycleBuilder().apply(block).handlers
