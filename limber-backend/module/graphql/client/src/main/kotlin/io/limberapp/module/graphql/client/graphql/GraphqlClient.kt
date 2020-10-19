package io.limberapp.module.graphql.client.graphql

import com.fasterxml.jackson.module.kotlin.readValue
import com.google.inject.Inject
import io.limberapp.common.client.LimberHttpClient
import io.limberapp.common.client.LimberHttpClientRequestBuilder
import io.limberapp.module.graphql.api.graphql.GraphqlApi

class GraphqlClient @Inject constructor(private val httpClient: LimberHttpClient) {
  @Suppress("OptionalUnit")
  suspend operator fun invoke(
      endpoint: GraphqlApi,
      builder: LimberHttpClientRequestBuilder.() -> Unit = {},
  ): Unit =
      httpClient.request(endpoint, builder) { readValue(checkNotNull(it)) }
}
