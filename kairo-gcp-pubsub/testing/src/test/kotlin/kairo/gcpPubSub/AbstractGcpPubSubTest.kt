package kairo.gcpPubSub

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.junit.jupiter.api.Test

/**
 * Shared tests for [GcpPubSub] implementations.
 * Ensures the in-memory fake and local implementations behave consistently.
 */
internal abstract class AbstractGcpPubSubTest {
  protected abstract val testTopic: String
  protected abstract val gcpPubSub: GcpPubSub

  protected abstract fun getPublishedMessages(): List<Pair<String, GcpPubSubMessage>>

  protected abstract fun resetPubSub()

  /**
   * Subscribe in a way that messages published to [testTopic] reach the handler.
   * Each implementation handles routing differently.
   */
  protected abstract fun subscribeToTestTopic(handler: suspend (GcpPubSubMessage) -> Unit)

  @Test
  fun `publish returns non-empty message ID`(): Unit =
    runTest {
      val messageId = gcpPubSub.publish(testTopic, GcpPubSubMessage(data = "hello"))
      messageId.shouldNotBe("")
    }

  @Test
  fun `publish returns unique message IDs`(): Unit =
    runTest {
      val id1 = gcpPubSub.publish(testTopic, GcpPubSubMessage(data = "msg1"))
      val id2 = gcpPubSub.publish(testTopic, GcpPubSubMessage(data = "msg2"))
      id1.shouldNotBe(id2)
    }

  @Test
  fun `published messages are stored`(): Unit =
    runTest {
      gcpPubSub.publish(testTopic, GcpPubSubMessage(data = "hello"))
      val messages = getPublishedMessages()
      messages.shouldHaveSize(1)
      messages[0].first.shouldBe(testTopic)
      messages[0].second.data.shouldBe("hello")
    }

  @Test
  fun `multiple messages are stored in order`(): Unit =
    runTest {
      gcpPubSub.publish(testTopic, GcpPubSubMessage(data = "first"))
      gcpPubSub.publish(testTopic, GcpPubSubMessage(data = "second"))
      val messages = getPublishedMessages()
      messages.shouldHaveSize(2)
      messages[0].second.data.shouldBe("first")
      messages[1].second.data.shouldBe("second")
    }

  @Test
  fun `publish preserves attributes`(): Unit =
    runTest {
      val attributes = mapOf("eventType" to "order.created", "version" to "1")
      gcpPubSub.publish(testTopic, GcpPubSubMessage(data = "hello", attributes = attributes))
      val messages = getPublishedMessages()
      messages[0].second.attributes.shouldBe(attributes)
    }

  @Test
  fun `publish preserves ordering key`(): Unit =
    runTest {
      gcpPubSub.publish(testTopic, GcpPubSubMessage(data = "hello", orderingKey = "order-123"))
      val messages = getPublishedMessages()
      messages[0].second.orderingKey.shouldBe("order-123")
    }

  @Test
  fun `publish with null ordering key`(): Unit =
    runTest {
      gcpPubSub.publish(testTopic, GcpPubSubMessage(data = "hello"))
      val messages = getPublishedMessages()
      messages[0].second.orderingKey.shouldBe(null)
    }

  @Test
  fun `publish with empty attributes`(): Unit =
    runTest {
      gcpPubSub.publish(testTopic, GcpPubSubMessage(data = "hello"))
      val messages = getPublishedMessages()
      messages[0].second.attributes.shouldBe(emptyMap())
    }

  @Test
  fun `reset clears published messages`(): Unit =
    runTest {
      gcpPubSub.publish(testTopic, GcpPubSubMessage(data = "hello"))
      getPublishedMessages().shouldHaveSize(1)
      resetPubSub()
      getPublishedMessages().shouldBeEmpty()
    }

  @Test
  fun `subscribe receives published messages`(): Unit =
    runTest {
      val received = CompletableDeferred<GcpPubSubMessage>()
      subscribeToTestTopic { message ->
        received.complete(message)
      }
      gcpPubSub.publish(testTopic, GcpPubSubMessage(data = "hello"))
      val message = withContext(Dispatchers.Default) { withTimeout(1_000) { received.await() } }
      message.data.shouldBe("hello")
    }

  @Test
  fun `subscribe receives attributes`(): Unit =
    runTest {
      val received = CompletableDeferred<GcpPubSubMessage>()
      subscribeToTestTopic { message ->
        received.complete(message)
      }
      val attributes = mapOf("key" to "value")
      gcpPubSub.publish(testTopic, GcpPubSubMessage(data = "hello", attributes = attributes))
      val message = withContext(Dispatchers.Default) { withTimeout(1_000) { received.await() } }
      message.attributes.shouldBe(attributes)
    }

  @Test
  fun `subscribe receives multiple messages`(): Unit =
    runTest {
      val received = mutableListOf<GcpPubSubMessage>()
      val allReceived = CompletableDeferred<Unit>()
      subscribeToTestTopic { message ->
        synchronized(received) { received.add(message) }
        if (received.size == 2) allReceived.complete(Unit)
      }
      gcpPubSub.publish(testTopic, GcpPubSubMessage(data = "first"))
      gcpPubSub.publish(testTopic, GcpPubSubMessage(data = "second"))
      withContext(Dispatchers.Default) { withTimeout(1_000) { allReceived.await() } }
      received.shouldHaveSize(2)
    }

  @Test
  fun `concurrent publishes are all recorded`(): Unit =
    runTest {
      val messageIds = (1..10).map { i ->
        async { gcpPubSub.publish(testTopic, GcpPubSubMessage(data = "msg-$i")) }
      }.awaitAll()
      messageIds.distinct().shouldHaveSize(10)
      getPublishedMessages().shouldHaveSize(10)
      getPublishedMessages().map { it.second.data }
        .shouldContainExactlyInAnyOrder((1..10).map { "msg-$it" })
    }

  @Test
  fun `handler that suspends completes correctly`(): Unit =
    runTest {
      val received = CompletableDeferred<GcpPubSubMessage>()
      subscribeToTestTopic { message ->
        delay(50)
        received.complete(message)
      }
      gcpPubSub.publish(testTopic, GcpPubSubMessage(data = "delayed"))
      val message = withContext(Dispatchers.Default) { withTimeout(2_000) { received.await() } }
      message.data.shouldBe("delayed")
    }

  @Test
  fun `multiple subscribers all receive the same message`(): Unit =
    runTest {
      val received1 = CompletableDeferred<GcpPubSubMessage>()
      val received2 = CompletableDeferred<GcpPubSubMessage>()
      subscribeToTestTopic { message -> received1.complete(message) }
      subscribeToTestTopic { message -> received2.complete(message) }
      gcpPubSub.publish(testTopic, GcpPubSubMessage(data = "broadcast"))
      withContext(Dispatchers.Default) {
        val msg1 = withTimeout(1_000) { received1.await() }
        val msg2 = withTimeout(1_000) { received2.await() }
        msg1.data.shouldBe("broadcast")
        msg2.data.shouldBe("broadcast")
      }
    }

  @Test
  fun `close does not throw`(): Unit =
    runTest {
      gcpPubSub.close()
    }
}
