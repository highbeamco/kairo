package kairo.admin.view

import kairo.admin.AdminDashboardConfig
import kotlinx.html.HTML
import kotlinx.html.a
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

@Suppress("LongMethod")
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
        classes = setOf("flex", "flex-col", "items-center", "mb-8")
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
          classes = setOf("text-sm", "text-gray-500", "mt-1")
          +"Sign in to continue."
        }
      }
      if (config.oauth != null) {
        a {
          href = "${config.pathPrefix}/login/oauth"
          classes = setOf(
            "w-full", "block", "text-center", "rounded-md", "bg-indigo-600", "px-4", "py-2",
            "text-sm", "font-medium", "text-white",
            "hover:bg-indigo-700", "focus:outline-none", "focus:ring-2", "focus:ring-indigo-500",
          )
          +"Sign in with ${config.oauth!!.providerName}"
        }
      } else {
        p {
          classes = setOf("text-sm", "text-red-600", "text-center")
          +"OAuth is not configured. Set AdminOAuthConfig to enable login."
        }
      }
    }
  }
}
