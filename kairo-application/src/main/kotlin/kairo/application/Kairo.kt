package kairo.application

import arrow.continuations.SuspendApp
import arrow.fx.coroutines.ResourceScope
import arrow.fx.coroutines.resourceScope
import kairo.server.Server
import kotlinx.coroutines.awaitCancellation

/** Manages the lifecycle of a Kairo application. Created by the [kairo] function. */
public class Kairo internal constructor(
  private val resourceScope: ResourceScope,
) {
  /**
   * Starts the Server and suspends until JVM termination (SIGTERM, SIGINT).
   *
   * @param release Called during shutdown. Should call [Server.stop] and clean up other resources.
   */
  public suspend fun Server.startAndWait(
    release: suspend () -> Unit,
  ) {
    resourceScope.install(
      acquire = { start() },
      release = { _, _ -> release() },
    )
    awaitCancellation()
  }
}

/**
 * Entrypoint for running a Kairo application.
 * Creates a resource scope, builds a [Kairo] instance, and blocks until JVM termination.
 */
public fun kairo(block: suspend Kairo.() -> Unit) {
  SuspendApp {
    resourceScope {
      val kairo = Kairo(this)
      kairo.block()
    }
  }
}
