package limber.service.healthCheck

import com.google.inject.Inject
import limber.client.HealthCheckClient
import limber.model.HealthCheck
import limber.service.HealthCheckService

internal class HealthCheckServiceImpl @Inject constructor(
  healthCheckClient: HealthCheckClient,
) : HealthCheckService(healthCheckClient) {
  override val healthChecks: Map<String, HealthCheck> = mapOf(
    "http" to HealthCheck(::httpHealthCheck),
    "server" to HealthCheck(::serverHealthCheck),
  )
}
