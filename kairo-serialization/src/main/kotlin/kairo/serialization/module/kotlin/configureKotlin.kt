package kairo.serialization.module.kotlin

import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.kotlinModule
import kairo.serialization.ObjectMapperFactoryBuilder

/**
 * Besides just installing the Kotlin module,
 * we change a few config params to have more sensible values.
 */
internal fun ObjectMapperFactoryBuilder.configureKotlin() {
  addModule(
    kotlinModule {
      configure(KotlinFeature.SingletonSupport, true)
      configure(KotlinFeature.StrictNullChecks, true)
    },
  )
}
