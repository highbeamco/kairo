package kairo.sqlMigration

import com.google.inject.Binder
import com.google.inject.Injector
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kairo.dependencyInjection.bind
import kairo.dependencyInjection.getInstance
import kairo.dependencyInjection.toProvider
import kairo.feature.Feature
import kairo.feature.FeaturePriority
import org.flywaydb.core.Flyway

private val logger: KLogger = KotlinLogging.logger {}

public open class KairoSqlMigrationFeature(
  private val config: KairoSqlMigrationConfig,
) : Feature() {
  override val name: String = "SQL Migration"

  override val priority: FeaturePriority = FeaturePriority.Framework

  override fun bind(binder: Binder) {
    binder.bind<KairoSqlMigrationConfig>().toInstance(config)
    binder.bind<Flyway>().toProvider(FlywayProvider::class)
  }

  override fun start(injector: Injector, features: Set<Feature>) {
    logger.info { "Running SQL migrations." }
    val flyway = injector.getInstance<Flyway>()
    flyway.migrate()
  }
}
