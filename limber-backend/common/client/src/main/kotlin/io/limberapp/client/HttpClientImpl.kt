package io.limberapp.client

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.ResponseException
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.limberapp.client.exception.LimberHttpClientException
import io.limberapp.restInterface.Endpoint
import io.limberapp.serialization.LimberObjectMapper
import io.ktor.client.HttpClient as KtorHttpClient

/**
 * The primary constructor should only be used for testing. There's no need to specify an engine
 * factory in production code - [CIO] will be used.
 *
 * Note: This implementation results in duplicate Accept headers. [JsonFeature] expects us to set
 * [JsonFeature.acceptContentTypes], but it can't be set to an empty list. [HttpClientImpl] manages
 * the Accept header on a per-request basis, so we end up doubling up the header. e.g. passing in an
 * Accept value of text/csv will result in Accept=[text/csv, application/json], and passing in an
 * Accept value of application/json will result in Accept=[application/json, application/json].
 */
class HttpClientImpl internal constructor(
    engineFactory: HttpClientEngineFactory<*>,
    private val baseUrl: String,
    objectMapper: LimberObjectMapper,
) : HttpClient(objectMapper) {
  constructor(baseUrl: String, objectMapper: LimberObjectMapper) : this(CIO, baseUrl, objectMapper)

  private val httpClient: KtorHttpClient = KtorHttpClient(engineFactory) {
    install(JsonFeature) {
      serializer = JacksonSerializer(objectMapper)
    }
  }

  override suspend fun request(
      endpoint: Endpoint,
      builder: RequestBuilder?,
  ): Pair<HttpStatusCode, String> {
    val httpResponse = try {
      httpClient.request<HttpResponse> {
        method = endpoint.httpMethod
        url(baseUrl + endpoint.href)
        val requestBuilder = LimberHttpClientRequestBuilder(accept = endpoint.contentType).apply {
          builder?.let { it() }
        }
        requestBuilder.headers.forEach { (key, value) -> header(key, value) }
        endpoint.body?.let {
          contentType(ContentType.Application.Json)
          body = it
        }
      }
    } catch (e: ResponseException) {
      val statusCode = e.response.status
      val responseBody = e.response.readText()
      throw LimberHttpClientException(statusCode, responseBody)
    }
    return Pair(httpResponse.status, httpResponse.readText())
  }

  override fun close(): Unit = httpClient.close()
}