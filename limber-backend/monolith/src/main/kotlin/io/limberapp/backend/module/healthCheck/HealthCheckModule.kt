package io.limberapp.backend.module.healthCheck

import io.limberapp.backend.module.healthCheck.endpoint.healthCheck.HealthCheck
import io.limberapp.backend.module.healthCheck.service.healthCheck.HealthCheckService
import io.limberapp.backend.module.healthCheck.service.healthCheck.HealthCheckServiceImpl
import io.limberapp.common.endpoint.ApiEndpoint
import io.limberapp.common.module.Module
import kotlinx.serialization.modules.EmptySerializersModule

/**
 * The health check module contains a health check that should indicate whether or not the service is healthy, taking
 * into account resources that all other modules use. This is why it's defined directly in the application rather than
 * in its own module module.
 */
internal class HealthCheckModule : Module() {
  override val serializersModule = EmptySerializersModule

  override val endpoints: List<Class<out ApiEndpoint<*, *, *>>> = listOf(HealthCheck::class.java)

  override fun bindServices() {
    bind(HealthCheckService::class, HealthCheckServiceImpl::class)
  }
}