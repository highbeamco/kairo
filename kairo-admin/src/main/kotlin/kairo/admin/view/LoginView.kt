package kairo.admin.view

import kairo.admin.AdminDashboardConfig
import kotlinx.html.HTML
import kotlinx.html.body
import kotlinx.html.button
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h2
import kotlinx.html.head
import kotlinx.html.img
import kotlinx.html.link
import kotlinx.html.meta
import kotlinx.html.p
import kotlinx.html.textArea
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
          +"Paste a valid bearer token to continue."
        }
      }
      form {
        method = kotlinx.html.FormMethod.post
        action = "${config.pathPrefix}/login"
        div {
          classes = setOf("mb-4")
          textArea {
            name = "token"
            rows = "4"
            classes = setOf(
              "w-full", "rounded-md", "border", "border-gray-300", "px-3", "py-2",
              "text-sm", "font-mono", "focus:outline-none", "focus:ring-2", "focus:ring-indigo-500",
              "placeholder-gray-400",
            )
            placeholder = "eyJhbGciOiJSUzI1NiIs..."
          }
        }
        button {
          type = kotlinx.html.ButtonType.submit
          classes = setOf(
            "w-full", "rounded-md", "bg-indigo-600", "px-4", "py-2",
            "text-sm", "font-medium", "text-white",
            "hover:bg-indigo-700", "focus:outline-none", "focus:ring-2", "focus:ring-indigo-500",
          )
          +"Sign in"
        }
      }
    }
  }
}
