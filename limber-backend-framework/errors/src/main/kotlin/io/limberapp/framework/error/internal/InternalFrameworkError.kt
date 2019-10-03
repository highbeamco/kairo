package io.limberapp.framework.error.internal

import io.limberapp.framework.error.FrameworkError

/**
 * An error representing that something went wrong internally. It's the code's fault, not the
 * caller's fault.
 */
data class InternalFrameworkError(
    override val message: String
) : FrameworkError {
    override val key = FrameworkError.Key.INTERNAL
}
