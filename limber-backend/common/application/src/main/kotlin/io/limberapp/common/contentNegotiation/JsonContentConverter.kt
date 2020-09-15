package io.limberapp.common.contentNegotiation

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.features.ContentConverter
import io.ktor.http.ContentType
import io.ktor.request.ApplicationReceiveRequest
import io.ktor.request.contentCharset
import io.ktor.util.pipeline.PipelineContext
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.jvm.javaio.toInputStream
import io.limberapp.common.serialization.Json
import kotlin.reflect.full.isSuperclassOf

class JsonContentConverter(private val json: Json) : ContentConverter {
  override suspend fun convertForReceive(context: PipelineContext<ApplicationReceiveRequest, ApplicationCall>): Any? {
    val request = context.subject
    val type = request.type
    val value = request.value as? ByteReadChannel ?: return null
    val charset = context.call.request.contentCharset() ?: Charsets.UTF_8
    val reader = value.toInputStream().reader(charset)
    val string = reader.readText()
    return when {
      List::class.isSuperclassOf(type) -> throw UnsupportedOperationException() // Unsupported for now.
      else -> json.parse(string, type)
    }
  }

  override suspend fun convertForSend(
    context: PipelineContext<Any, ApplicationCall>,
    contentType: ContentType,
    value: Any,
  ): Any? {
    return when (value) {
      is Set<*> -> json.stringifySet(value)
      is List<*> -> json.stringifyList(value)
      else -> json.stringify(value)
    }
  }
}
