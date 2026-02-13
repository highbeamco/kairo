package kairo.testing

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/** Semantic wrapper for test setup. Runs before the test action. */
@OptIn(ExperimentalContracts::class)
public inline fun <T, R> T.setup(
  @Suppress("UNUSED_PARAMETER")
  description: String? = null, // For readability on the caller's side.
  block: T.() -> R,
): R {
  contract {
    callsInPlace(block, InvocationKind.EXACTLY_ONCE)
  }
  return block()
}

/** Semantic wrapper for verifying preconditions before the test action. */
@OptIn(ExperimentalContracts::class)
public inline fun <T, R> T.precondition(
  @Suppress("UNUSED_PARAMETER")
  description: String? = null, // For readability on the caller's side.
  block: T.() -> R,
): R {
  contract {
    callsInPlace(block, InvocationKind.EXACTLY_ONCE)
  }
  return block()
}

/** Semantic wrapper for the primary test action and assertions. */
@OptIn(ExperimentalContracts::class)
public inline fun <T, R> T.test(
  @Suppress("UNUSED_PARAMETER")
  description: String? = null, // For readability on the caller's side.
  block: T.() -> R,
): R {
  contract {
    callsInPlace(block, InvocationKind.EXACTLY_ONCE)
  }
  return block()
}

/** Semantic wrapper for verifying side effects after the test action. */
@OptIn(ExperimentalContracts::class)
public inline fun <T, R> T.postcondition(
  @Suppress("UNUSED_PARAMETER")
  description: String? = null, // For readability on the caller's side.
  block: T.() -> R,
): R {
  contract {
    callsInPlace(block, InvocationKind.EXACTLY_ONCE)
  }
  return block()
}
