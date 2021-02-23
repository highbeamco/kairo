package io.limberapp.backend.config

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import io.limberapp.common.config.ConfigStringDeserializer

/**
 * This class encapsulates the configuration for the connection to an SQL database.
 */
data class SqlDatabaseConfig(
    @JsonDeserialize(using = ConfigStringDeserializer::class)
    val jdbcUrl: String,
    val defaultSchema: String? = null,
    @JsonDeserialize(using = ConfigStringDeserializer::class)
    val username: String,
    @JsonDeserialize(using = ConfigStringDeserializer::class)
    val password: String?,
    val connectionTimeout: Long? = null,
    val minimumIdle: Int? = null,
    val maximumPoolSize: Int? = null,
    val properties: Map<String, String> = emptyMap(),
)
