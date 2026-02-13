package kairo.datetime

import kotlin.time.Instant

/** Shorthand for the Unix epoch (1970-01-01T00:00:00Z). Useful in tests for deterministic timestamps. */
public val Instant.Companion.epoch: Instant
  get() = fromEpochMilliseconds(0)
