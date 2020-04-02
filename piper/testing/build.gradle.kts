plugins {
    kotlin("jvm")
    id(Plugins.detekt)
}

tasks.compileKotlin {
    kotlinOptions.jvmTarget = "1.8"
}
tasks.compileTestKotlin {
    kotlinOptions.jvmTarget = "1.8"
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
}
