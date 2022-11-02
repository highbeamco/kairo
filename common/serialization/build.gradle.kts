import limber.gradle.Dependencies
import limber.gradle.plugin.main

plugins {
  id("limber-jvm")
}

main {
  dependencies {
    api(project(":common:serialization:interface"))
    api(Dependencies.Jackson.databind)
    implementation(Dependencies.Jackson.DataFormat.yaml)
    implementation(Dependencies.Jackson.DataType.jsr310)
    api(Dependencies.Jackson.Module.kotlin) // Exposed for access to extension functions like [readValue].
  }
}
