package kairo.gcpPubSub

import com.google.api.core.ApiFutureToListenableFuture
import com.google.api.gax.batching.BatchingSettings
import com.google.api.gax.batching.FlowControlSettings
import com.google.cloud.pubsub.v1.MessageReceiver
import com.google.cloud.pubsub.v1.Publisher
import com.google.cloud.pubsub.v1.Subscriber
import com.google.protobuf.ByteString
import com.google.pubsub.v1.ProjectSubscriptionName
import com.google.pubsub.v1.PubsubMessage
import com.google.pubsub.v1.TopicName
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch
import org.threeten.bp.Duration as ThreeTenDuration

private val logger: KLogger = KotlinLogging.logger {}

public class DefaultGcpPubSub(
  private val config: DefaultGcpPubSubConfig,
  private val configurePublisher: Publisher.Builder.() -> Unit = {},
  private val configureSubscriber: Subscriber.Builder.() -> Unit = {},
) : GcpPubSub() {
  private val scope: CoroutineScope = CoroutineScope(
    SupervisorJob() +
      (
        config.concurrency.handlerParallelism
          ?.let { Dispatchers.Default.limitedParallelism(it) }
          ?: Dispatchers.Default
        ),
  )
  private val publishers: ConcurrentHashMap<String, Publisher> = ConcurrentHashMap()
  private val subscribers: MutableList<Subscriber> = mutableListOf()

  override suspend fun publish(topic: String, message: GcpPubSubMessage): String {
    logger.debug { "Publishing message to topic: $topic." }
    val publisher = publishers.computeIfAbsent(topic) {
      createPublisher(topic)
    }
    val pubsubMessage = PubsubMessage.newBuilder().apply {
      data = ByteString.copyFromUtf8(message.data)
      putAllAttributes(message.attributes)
      message.orderingKey?.let { orderingKey = it }
    }.build()
    return ApiFutureToListenableFuture(publisher.publish(pubsubMessage)).await()
  }

  override fun subscribe(subscription: String, handler: suspend (GcpPubSubMessage) -> Unit) {
    logger.info { "Subscribing to subscription: $subscription." }
    val receiver = MessageReceiver { message, consumer ->
      scope.launch {
        try {
          handler(
            GcpPubSubMessage(
              data = message.data.toStringUtf8(),
              attributes = message.attributesMap,
              orderingKey = message.orderingKey.takeIf { it.isNotEmpty() },
            ),
          )
          consumer.ack()
        } catch (e: CancellationException) {
          throw e
        } catch (e: Exception) {
          logger.error(e) { "Error processing message from subscription: $subscription." }
          consumer.nack()
        }
      }
    }
    val subscriber = createSubscriber(subscription, receiver)
    synchronized(subscribers) {
      subscribers.add(subscriber)
    }
    subscriber.startAsync()
  }

  override fun close() {
    scope.cancel()
    publishers.values.forEach { it.shutdown() }
    synchronized(subscribers) {
      subscribers.forEach { it.stopAsync() }
    }
  }

  private fun createPublisher(topic: String): Publisher =
    Publisher.newBuilder(TopicName.of(config.projectId, topic)).apply {
      val batching = config.publishing.batching
      if (batching.delayThreshold != null ||
        batching.elementCountThreshold != null ||
        batching.requestBytesThreshold != null
      ) {
        val batchingSettings = BatchingSettings.newBuilder().apply {
          batching.delayThreshold?.let {
            setDelayThreshold(ThreeTenDuration.ofMillis(it.inWholeMilliseconds))
          }
          batching.elementCountThreshold?.let { setElementCountThreshold(it) }
          batching.requestBytesThreshold?.let { setRequestByteThreshold(it) }
        }.build()
        setBatchingSettings(batchingSettings)
      }
      setEnableMessageOrdering(config.publishing.enableMessageOrdering)
      configurePublisher()
    }.build()

  private fun createSubscriber(subscription: String, receiver: MessageReceiver): Subscriber =
    Subscriber.newBuilder(
      ProjectSubscriptionName.of(config.projectId, subscription),
      receiver,
    ).apply {
      val flowControl = config.subscribing.flowControl
      if (flowControl.maxOutstandingElementCount != null ||
        flowControl.maxOutstandingRequestBytes != null
      ) {
        val flowControlSettings = FlowControlSettings.newBuilder().apply {
          flowControl.maxOutstandingElementCount?.let { setMaxOutstandingElementCount(it) }
          flowControl.maxOutstandingRequestBytes?.let { setMaxOutstandingRequestBytes(it) }
        }.build()
        setFlowControlSettings(flowControlSettings)
      }
      config.subscribing.maxAckExtensionPeriod?.let {
        setMaxAckExtensionPeriod(ThreeTenDuration.ofMillis(it.inWholeMilliseconds))
      }
      config.subscribing.parallelPullCount?.let { setParallelPullCount(it) }
      configureSubscriber()
    }.build()
}
