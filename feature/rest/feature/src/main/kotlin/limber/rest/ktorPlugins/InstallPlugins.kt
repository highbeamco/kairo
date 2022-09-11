package limber.rest.ktorPlugins

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.Injector
import io.ktor.server.application.Application

/**
 * The list of plugins can be seen on the project generator site, https://start.ktor.io/.
 * The categories below match the categories from that site.
 */
internal fun Application.installPlugins(injector: Injector) {
  val objectMapper = injector.getInstance(ObjectMapper::class.java)

  installRoutingPlugins()
  installHttpPlugins(objectMapper)
}