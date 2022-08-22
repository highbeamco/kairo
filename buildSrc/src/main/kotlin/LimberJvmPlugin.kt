import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.repositories
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

/**
 * Configures JVM Gradle modules.
 * Unless and until Multiplatform modules are introduced,
 * this should be used in all Gradle modules.
 */
class LimberJvmPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    configureIdea(target)
    configureJvm(target)
  }

  private fun configureIdea(target: Project) {
    target.pluginManager.apply("org.gradle.idea")
  }

  private fun configureJvm(target: Project) {
    target.pluginManager.apply("org.jetbrains.kotlin.jvm")
    target.repositories {
      mavenCentral()
    }
    target.extensions.configure<JavaPluginExtension> {
      toolchain {
        languageVersion.set(JavaLanguageVersion.of(JavaVersion.VERSION_17.toString()))
      }
    }
    target.extensions.configure<KotlinJvmProjectExtension> {
      explicitApi()
    }
    target.dependencies {
      add("implementation", kotlin("reflect"))
    }
  }
}