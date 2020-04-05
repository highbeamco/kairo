plugins {
    kotlin("jvm")
    id(Plugins.detekt)
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    api(kotlin("test"))
    api(kotlin("test-junit5"))
    api(project(":piper"))
    api(Dependencies.JUnit.api)
    runtimeElements(Dependencies.JUnit.engine)
    api(Dependencies.Ktor.test)
    api(Dependencies.MockK.mockK)
}

detekt {
    config = files("$rootDir/.detekt/config.yml")
    input = files("src/main/kotlin", "src/test/kotlin")
}
