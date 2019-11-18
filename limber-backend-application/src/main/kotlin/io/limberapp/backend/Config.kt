package io.limberapp.backend

import io.limberapp.framework.config.Config
import io.limberapp.framework.config.database.MongoDatabaseConfig
import io.limberapp.framework.config.jwt.JwtConfig
import io.limberapp.framework.config.serving.ServingConfig

/**
 * The Config class contains all custom configuration for the app. It doesn't contain Ktor built-in
 * configuration.
 */
data class Config(
    val mongoDatabase: MongoDatabaseConfig,
    override val jwt: JwtConfig,
    override val serving: ServingConfig
) : Config
