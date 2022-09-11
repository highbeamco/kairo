plugins {
  id("limber-jvm")
}

dependencies {
  api(project(":common:feature"))
  api(project(":feature:rest:interface"))

  // Ktor core.
  api(Dependencies.Ktor.Server.coreJvm)
  implementation(Dependencies.Ktor.Server.cio)
  implementation(Dependencies.Ktor.Server.hostCommonJvm)

  // Ktor plugins.
  implementation(Dependencies.Ktor.Serialization.jackson)
  implementation(Dependencies.Ktor.Server.autoHeadResponse)
  implementation(Dependencies.Ktor.Server.contentNegotiation)
  implementation(Dependencies.Ktor.Server.dataConversion)
  implementation(Dependencies.Ktor.Server.doubleReceive)
  implementation(Dependencies.Ktor.Server.statusPages)

  // MockK is used in production code for endpoint template generation.
  implementation(Dependencies.Testing.mockK)
}