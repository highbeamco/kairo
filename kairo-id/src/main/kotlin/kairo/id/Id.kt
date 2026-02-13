package kairo.id

import kotlin.random.Random

/**
 * Kairo IDs are an alternative to raw UUIDs or serial IDs.
 * Semantic prefixes (user, business) tell you what they represent.
 * Strong entropy (as much or more randomness than UUIDs, tunable by payload length).
 * Compile-time safety without runtime overhead.
 *
 * An example Kairo ID is "user_ccU4Rn4DKVjCMqt3d0oAw3".
 * Prefix: "user". Payload: "ccU4Rn4DKVjCMqt3d0oAw3".
 */
public interface Id {
  public val value: String

  /**
   * Base companion for ID types. Provides [random] generation and [regex] validation helpers.
   * The payload length (5-32 characters) defaults to 15 (~89 bits of entropy).
   */
  public abstract class Companion<T : Id>(
    private val length: Int = 15,
  ) {
    init {
      require(length in 5..32) { "Invalid ID length (length=$length). Must be between 5 and 32 (inclusive)." }
    }

    /** Generates a new random ID with a base-62 encoded payload. */
    public fun random(): T {
      val payload = buildString {
        repeat(this@Companion.length) {
          val char = when (val seed = Random.nextInt(62)) {
            in 0..9 -> Char(48 + seed) // 0-9
            in 10..35 -> Char(65 - 10 + seed) // A-Z
            in 36..61 -> Char(97 - 36 + seed) // a-z
            else -> error("Implementation error.")
          }
          append(char)
        }
      }
      return create(payload)
    }

    /** Constructs an ID instance from the given payload string. Implemented by each ID type. */
    protected abstract fun create(payload: String): T

    /** Builds a validation regex matching the given prefix, an underscore, and a base-62 payload of the configured length. */
    protected fun regex(prefix: Regex): Regex =
      Regex("(?<prefix>(?=[a-z][a-z0-9]*(_[a-z][a-z0-9]*)*)$prefix)_(?<payload>[A-Za-z0-9]+)")
  }
}
