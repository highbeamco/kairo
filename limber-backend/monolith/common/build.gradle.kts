plugins {
  kotlin("jvm")
  kotlin("plugin.serialization")
  id(Plugins.detekt)
}

dependencies {
  implementation(project(":limber-backend:common:serialization"))
  implementation(project(":limber-backend:common:types"))
  implementation(project(":limber-backend:common:util"))
}

detekt {
  config = files("$rootDir/.detekt/config.yaml")
  input = files("src/main/kotlin", "src/test/kotlin")
}
