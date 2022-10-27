package limber.endpoint.healthCheck

import io.kotest.matchers.shouldBe
import limber.api.healthCheck.HealthCheckApi
import limber.testing.IntegrationTest
import limber.testing.test
import org.junit.jupiter.api.Test

internal class GetHealthCheckLivenessTest : IntegrationTest() {
  @Test
  fun healthy() {
    test {
      healthCheckClient(HealthCheckApi.GetLiveness)
        .shouldBe(Unit)
    }
  }
}