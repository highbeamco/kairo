plugins {
    kotlin("jvm")
    id(Plugins.detekt)
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    api(project(":piper:errors"))
    api(project(":piper:exceptions"))
    api(project(":piper:util"))
    implementation(Dependencies.Ktor.httpJvm)
}

detekt {
    config = files("$rootDir/.detekt/config.yml")
    input = files("src/main/kotlin", "src/test/kotlin")
}
