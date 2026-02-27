package kairo.gcpPubSub

import com.google.cloud.pubsub.v1.Publisher
import com.google.cloud.pubsub.v1.Subscriber
import kairo.dependencyInjection.HasKoinModules
import kairo.feature.Feature
import kairo.feature.LifecycleHandler
import kairo.feature.lifecycle
import org.koin.core.module.Module
import org.koin.dsl.module

public class GcpPubSubFeature(
  config: GcpPubSubFeatureConfig,
  configurePublisher: Publisher.Builder.() -> Unit = {},
  configureSubscriber: Subscriber.Builder.() -> Unit = {},
) : Feature(), HasKoinModules {
  override val name: String = "GCP Pub/Sub"

  private val gcpPubSub: GcpPubSub by lazy {
    when (config) {
      is GcpPubSubFeatureConfig.Real ->
        DefaultGcpPubSub(
          config = config.defaultGcpPubSub,
          configurePublisher = configurePublisher,
          configureSubscriber = configureSubscriber,
        )
      is GcpPubSubFeatureConfig.Local ->
        LocalGcpPubSub(topicToSubscriptions = config.topicToSubscriptions)
    }
  }

  override val koinModules: List<Module> =
    listOf(
      module {
        single<GcpPubSub> { gcpPubSub }
      },
    )

  override val lifecycle: List<LifecycleHandler> =
    lifecycle {
      handler {
        start { gcpPubSub }
        stop { gcpPubSub.close() }
      }
    }
}
