package limber.testing

import limber.client.HealthCheckClient
import limber.config.HealthCheckFeatureTestConfig
import limber.feature.HealthCheckFeature
import limber.feature.TestRestFeature
import limber.service.TestHealthCheckService

private val config = HealthCheckFeatureTestConfig

private const val PORT: Int = 8081

internal abstract class IntegrationTest : FeatureIntegrationTest(
  config = config,
  featureUnderTest = HealthCheckFeature(
    serviceKClass = TestHealthCheckService::class,
    baseUrl = "http://localhost:$PORT",
  ),
  supportingFeatures = setOf(TestRestFeature(port = PORT)),
) {
  protected val healthCheckClient: HealthCheckClient =
    injector.getInstance(HealthCheckClient::class.java)
}
