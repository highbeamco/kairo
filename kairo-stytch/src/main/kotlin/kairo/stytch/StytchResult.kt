package kairo.stytch

import com.stytch.java.common.StytchResult

/** Unwraps a [StytchResult], returning the value for Success or throwing for Error. */
public fun <T> StytchResult<T>.get(): T =
  when (this) {
    is StytchResult.Success -> value
    is StytchResult.Error -> throw exception
  }
