import limber.gradle.plugin.main

plugins {
  id("limber-jvm")
}

main {
  dependencies {
    api(project(":feature:organization:rest-interface"))
  }
}
