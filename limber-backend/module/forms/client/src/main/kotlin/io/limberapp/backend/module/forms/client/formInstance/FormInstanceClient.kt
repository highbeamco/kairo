package io.limberapp.backend.module.forms.client.formInstance

import io.limberapp.backend.module.forms.api.formInstance.FormInstanceApi
import io.limberapp.backend.module.forms.rep.formInstance.FormInstanceRep
import io.limberapp.client.LimberHttpClient
import io.limberapp.client.LimberHttpClientRequestBuilder

class FormInstanceClient(private val httpClient: LimberHttpClient) {
  suspend operator fun invoke(
    endpoint: FormInstanceApi.Post,
    builder: LimberHttpClientRequestBuilder.() -> Unit = {},
  ) = httpClient.request(endpoint, builder) {
    parse<FormInstanceRep.Complete>(checkNotNull(it))
  }

  suspend operator fun invoke(
    endpoint: FormInstanceApi.Get,
    builder: LimberHttpClientRequestBuilder.() -> Unit = {},
  ) = httpClient.request(endpoint, builder) {
    it?.let { parse<FormInstanceRep.Complete>(it) }
  }

  suspend operator fun invoke(
    endpoint: FormInstanceApi.GetByFeatureGuid,
    builder: LimberHttpClientRequestBuilder.() -> Unit = {},
  ) = httpClient.request(endpoint, builder) {
    parse<List<FormInstanceRep.Summary>>(checkNotNull(it))
  }

  suspend operator fun invoke(
    endpoint: FormInstanceApi.ExportByFeatureGuid,
    builder: LimberHttpClientRequestBuilder.() -> Unit = {},
  ) = httpClient.request(endpoint, builder) {
    checkNotNull(it)
  }

  suspend operator fun invoke(
    endpoint: FormInstanceApi.Patch,
    builder: LimberHttpClientRequestBuilder.() -> Unit = {},
  ) = httpClient.request(endpoint, builder) {
    it?.let { parse<FormInstanceRep.Complete>(it) }
  }

  suspend operator fun invoke(
    endpoint: FormInstanceApi.Delete,
    builder: LimberHttpClientRequestBuilder.() -> Unit = {},
  ) = httpClient.request(endpoint, builder) {
    it?.let { parse<Unit>(it) }
  }
}