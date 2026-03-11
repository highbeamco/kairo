package kairo.gcpPubSub

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.junit.jupiter.api.Test

internal class LocalGcpPubSubTest : AbstractGcpPubSubTest() {
  override val testTopic: String = "test-topic"

  private val localPubSub: LocalGcpPubSub =
    LocalGcpPubSub(topicToSubscriptions = mapOf("test-topic" to listOf("test-sub")))

  override val gcpPubSub: GcpPubSub = localPubSub

  override fun getPublishedMessages(): List<Pair<String, GcpPubSubMessage>> =
    localPubSub.getPublishedMessages()

  override fun resetPubSub() {
    localPubSub.reset()
  }

  override fun subscribeToTestTopic(handler: suspend (GcpPubSubMessage) -> Unit) {
    localPubSub.subscribe("test-sub", handler)
  }

  @Test
  fun `slow handler does not block fast handler`(): Unit =
    runTest {
      val fastReceived = CompletableDeferred<GcpPubSubMessage>()
      val slowReceived = CompletableDeferred<GcpPubSubMessage>()
      subscribeToTestTopic { message ->
        delay(500)
        slowReceived.complete(message)
      }
      subscribeToTestTopic { message ->
        fastReceived.complete(message)
      }
      localPubSub.publish(testTopic, GcpPubSubMessage(data = "parallel"))
      withContext(Dispatchers.Default) {
        // The fast handler should complete without waiting for the slow handler.
        val fastMessage = withTimeout(200) { fastReceived.await() }
        fastMessage.data.shouldBe("parallel")
        // The slow handler should eventually complete too.
        val slowMessage = withTimeout(2_000) { slowReceived.await() }
        slowMessage.data.shouldBe("parallel")
      }
    }
}
