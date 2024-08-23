package kairo.clock

import com.google.inject.PrivateBinder
import java.time.Clock
import kairo.dependencyInjection.bind
import kairo.feature.Feature
import kairo.feature.FeaturePriority

public class KairoClockFeature(
  private val config: KairoClockConfig,
) : Feature() {
  override val name: String = "KairoClock"

  override val priority: FeaturePriority = FeaturePriority.Normal

  override fun bind(binder: PrivateBinder) {
    binder.bind<Clock>().toInstance(createClock(config))
  }

  private fun createClock(config: KairoClockConfig): Clock =
    when (config) {
      is KairoClockConfig.Fixed -> Clock.fixed(config.instant, config.timeZone)
      is KairoClockConfig.System -> Clock.system(config.timeZone)
    }
}