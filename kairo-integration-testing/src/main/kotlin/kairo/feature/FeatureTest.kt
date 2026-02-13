package kairo.feature

import kairo.dependencyInjection.KoinExtension
import kairo.server.Server
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.koin.core.KoinApplication

/**
 * Base class for Kairo integration tests.
 * Creates and starts a [Server] before each test, stops it after.
 * Extend this and implement [createServer] to define your test server.
 */
public abstract class FeatureTest : KoinExtension(), FeatureTestAware, AfterEachCallback {
  override fun beforeEach(context: ExtensionContext) {
    super.beforeEach(context)
    val server = createServer(context, checkNotNull(context.koin))
    context.server = server
    runBlocking {
      server.start()
    }
  }

  /** Creates the Server for this test. Called before each test method. */
  public abstract fun createServer(context: ExtensionContext, koinApplication: KoinApplication): Server

  override fun afterEach(context: ExtensionContext) {
    context.server?.let { server ->
      runBlocking {
        server.stop()
      }
    }
  }

  public companion object {
    /** JUnit extension namespace for storing test state. */
    public val namespace: ExtensionContext.Namespace =
      ExtensionContext.Namespace.create(FeatureTest::class)
  }
}
