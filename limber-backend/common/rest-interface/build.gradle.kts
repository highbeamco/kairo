plugins {
  kotlin("jvm")
  id(Plugins.detekt)
}

dependencies {
  implementation(kotlin("reflect"))

  api(project(":limber-backend:common:reps"))
  implementation(project(":limber-backend:common:util"))

  api(Dependencies.Ktor.httpJvm)
  implementation(Dependencies.Logging.slf4j)
}

detekt {
  config = files("$rootDir/.detekt/config.yaml")
  input = files("src/main/kotlin", "src/test/kotlin")
}
