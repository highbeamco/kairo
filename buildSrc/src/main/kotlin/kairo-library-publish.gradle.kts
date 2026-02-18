plugins {
  `java-library`
  `maven-publish`
}

java {
  withSourcesJar()
}

publishing {
  publications {
    repositories {
      highbeamKairo()
    }
    create<MavenPublication>("maven") {
      groupId = groupId()
      artifactId = artifactId(project.path)
      from(components["java"])
      license()
    }
  }
}

tasks.withType<PublishToMavenRepository> {
  doFirst {
    requireVersion(version)
  }
}
