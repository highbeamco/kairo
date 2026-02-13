package kairo.config

/**
 * Config resolvers let you dynamically resolve config string values.
 * String values that start with [prefix] will be mapped through [resolve].
 *
 * Resolvers run after HOCON loading. They match string values by prefix
 * and replace the entire value with the resolver's result.
 * If the resolver returns null, the original value (with prefix stripped) is used.
 */
@Suppress("UseDataClass")
public class ConfigResolver(
  public val prefix: String,
  public val resolve: suspend (raw: String) -> String?,
)
