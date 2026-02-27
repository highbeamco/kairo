package kairo.gcpPubSub

import kotlin.time.Duration

/**
 * Configuration for [DefaultGcpPubSub].
 * All nullable fields default to the GCP SDK's built-in defaults when null.
 */
public data class DefaultGcpPubSubConfig(
  val projectId: String,
  val concurrency: Concurrency = Concurrency(),
  val publishing: Publishing = Publishing(),
  val subscribing: Subscribing = Subscribing(),
) {
  public data class Concurrency(
    val handlerParallelism: Int? = null, // If null, uses Dispatchers.Default.
  )

  public data class Publishing(
    val batching: Batching = Batching(),
    val enableMessageOrdering: Boolean = false,
  ) {
    public data class Batching(
      val delayThreshold: Duration? = null, // If null, uses SDK default.
      val elementCountThreshold: Long? = null, // If null, uses SDK default.
      val requestBytesThreshold: Long? = null, // If null, uses SDK default.
    )
  }

  public data class Subscribing(
    val flowControl: FlowControl = FlowControl(),
    val maxAckExtensionPeriod: Duration? = null, // If null, uses SDK default.
    val parallelPullCount: Int? = null, // If null, uses SDK default.
  ) {
    public data class FlowControl(
      val maxOutstandingElementCount: Long? = null, // If null, uses SDK default.
      val maxOutstandingRequestBytes: Long? = null, // If null, uses SDK default.
    )
  }
}
