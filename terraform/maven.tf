resource "google_artifact_registry_repository" "maven_repo" {
  project = var.project
  repository_id = "maven"
  format = "MAVEN"
  location = "us-central1"
  maven_config {
    allow_snapshot_overwrites = false
    version_policy = "RELEASE"
  }
}

resource "google_artifact_registry_repository_iam_member" "maven_public" {
  repository = google_artifact_registry_repository.maven_repo.id
  member = "allUsers"
  role = "roles/artifactregistry.reader"
}
