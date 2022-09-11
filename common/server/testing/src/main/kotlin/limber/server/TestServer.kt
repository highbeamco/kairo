package limber.server

import limber.config.TestConfig
import limber.feature.Feature

public class TestServer(
  config: TestConfig,
  supportingFeatures: Set<Feature>,
  featureUnderTest: Feature,
) : Server<TestConfig>(config) {
  override val features: Set<Feature> = supportingFeatures + featureUnderTest

  init {
    start(wait = false)
  }
}