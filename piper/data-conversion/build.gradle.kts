plugins {
    kotlin("jvm")
    id("io.gitlab.arturbosch.detekt")
}

tasks.compileKotlin {
    kotlinOptions.jvmTarget = "1.8"
}
tasks.compileTestKotlin {
    kotlinOptions.jvmTarget = "1.8"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(project(":piper:util"))
    implementation(project(":piper:validation"))
}

detekt {
    config = files("$rootDir/.detekt/config.yml")
}
