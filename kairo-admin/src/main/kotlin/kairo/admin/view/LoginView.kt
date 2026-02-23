package kairo.admin.view

import kairo.admin.AdminDashboardConfig
import kotlinx.html.HTML
import kotlinx.html.body
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.h2
import kotlinx.html.head
import kotlinx.html.img
import kotlinx.html.link
import kotlinx.html.meta
import kotlinx.html.p
import kotlinx.html.title

/**
 * Rendered only when auth is required but OAuth is not configured.
 * When OAuth is configured, GET /login redirects straight to the provider.
 */
internal fun HTML.loginView(config: AdminDashboardConfig) {
  head {
    meta(charset = "utf-8")
    meta(name = "viewport", content = "width=device-width, initial-scale=1")
    title { +"${config.serverName ?: config.title} - Login" }
    link(rel = "icon", type = "image/png", href = "${config.pathPrefix}/static/img/logo.png")
    link(rel = "stylesheet", href = "${config.pathPrefix}/static/css/tailwind.css")
  }
  body {
    classes = setOf("bg-white", "min-h-screen", "flex", "items-center", "justify-center")
    div {
      classes = setOf("w-full", "max-w-md", "p-8")
      div {
        classes = setOf("flex", "flex-col", "items-center")
        img {
          src = "${config.pathPrefix}/static/img/logo.png"
          alt = "Logo"
          classes = setOf("w-12", "h-12", "mb-4")
        }
        h2 {
          classes = setOf("text-xl", "font-semibold", "text-gray-900")
          +"${config.serverName ?: config.title} Admin"
        }
        p {
          classes = setOf("text-sm", "text-red-600", "mt-4", "text-center")
          +"OAuth is not configured. Set AdminOAuthConfig to enable login."
        }
      }
    }
  }
}
