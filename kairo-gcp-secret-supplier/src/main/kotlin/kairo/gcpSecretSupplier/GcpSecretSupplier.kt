package kairo.gcpSecretSupplier

import kairo.protectedString.ProtectedString

/**
 * Lightweight and coroutine-friendly Google Secret Manager wrapper for Kotlin.
 * Use [DefaultGcpSecretSupplier] in production.
 */
public abstract class GcpSecretSupplier {
  /** Fetches a secret by its fully-qualified resource name. Returns null if the secret does not exist. */
  public abstract suspend operator fun get(id: String): ProtectedString?
}
