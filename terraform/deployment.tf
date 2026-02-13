resource "google_service_account" "deployment" {
  project = var.project
  account_id = "deployment"
  display_name = "Deployment"
}

resource "google_project_iam_member" "deployment" {
  for_each = toset([
    "roles/editor",
  ])
  project = var.project
  role = each.value
  member = "serviceAccount:${google_service_account.deployment.email}"
}
