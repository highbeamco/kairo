package kairo.healthCheck

import com.google.inject.Binder
import com.google.inject.Injector
import kairo.dependencyInjection.bind
import kairo.dependencyInjection.getInstance
import kairo.dependencyInjection.toClass
import kairo.feature.Feature
import kairo.feature.FeaturePriority
import kairo.rest.server.bindRestHandlers
import kotlin.reflect.KClass

public open class KairoHealthCheckFeature(
  private val healthCheckServiceClass: KClass<out HealthCheckService>,
) : Feature() {
  final override val name: String = "Health Check"

  final override val priority: FeaturePriority = FeaturePriority.Normal

  override fun bind(binder: Binder) {
    binder.bind<HealthCheckService>().toClass(healthCheckServiceClass).asEagerSingleton()
    binder.bindRestHandlers<HealthCheckHandler>()
  }

  override fun afterStart(injector: Injector) {
    val healthCheckService = injector.getInstance<HealthCheckService>()
    healthCheckService.status = HealthCheckRep.Status.Healthy
  }

  override fun beforeStop(injector: Injector?) {
    injector ?: return
    val healthCheckService = injector.getInstance<HealthCheckService>()
    healthCheckService.status = HealthCheckRep.Status.Unhealthy
  }
}
