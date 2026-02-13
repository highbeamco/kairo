resource "google_storage_bucket" "infrastructure" {
  project = var.project
  name = "highbeam-kairo-infrastructure"
  location = "US"
}
