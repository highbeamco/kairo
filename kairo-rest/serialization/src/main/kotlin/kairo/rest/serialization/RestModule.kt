package kairo.rest.serialization

import com.fasterxml.jackson.databind.module.SimpleModule
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode

/** Jackson module for serializing Ktor HTTP types ([HttpMethod] and [HttpStatusCode]). */
public class RestModule : SimpleModule() {
  init {
    addSerializer(HttpMethod::class.java, HttpMethodSerializer())
    addDeserializer(HttpMethod::class.java, HttpMethodDeserializer())

    addSerializer(HttpStatusCode::class.java, HttpStatusCodeSerializer())
    addDeserializer(HttpStatusCode::class.java, HttpStatusCodeDeserializer())
  }
}
