package kairo.rest.server

import io.ktor.http.*
import io.ktor.serialization.jackson.JacksonConverter
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.callid.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kairo.rest.ktorServerMapper
import java.util.*
import kotlin.uuid.Uuid

internal fun Application.installCallId() {
  install(CallId) {
    retrieveFromHeader(HttpHeaders.XRequestId)
    generate { Uuid.random().toString() }
  }
}
