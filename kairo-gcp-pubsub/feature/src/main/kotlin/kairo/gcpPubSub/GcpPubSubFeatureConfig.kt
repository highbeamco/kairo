package kairo.gcpPubSub

public sealed class GcpPubSubFeatureConfig {
  public data class Real(
    val defaultGcpPubSub: DefaultGcpPubSubConfig,
  ) : GcpPubSubFeatureConfig()

  public data class Local(
    val topicToSubscriptions: Map<String, List<String>> = emptyMap(),
  ) : GcpPubSubFeatureConfig()
}
