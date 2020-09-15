package io.limberapp.backend.module.monolith

import io.limberapp.backend.module.monolith.service.healthCheck.HealthCheckServiceImpl
import io.limberapp.common.endpoint.ApiEndpoint
import io.limberapp.common.module.Module
import io.limberapp.common.module.healthCheck.service.healthCheck.HealthCheckService
import kotlinx.serialization.modules.EmptySerializersModule

internal class MonolithModule : Module() {
  override val serializersModule = EmptySerializersModule

  override val endpoints = emptyList<Class<out ApiEndpoint<*, *, *>>>()

  override fun bindServices() {
    bind(HealthCheckService::class, HealthCheckServiceImpl::class)
  }
}
