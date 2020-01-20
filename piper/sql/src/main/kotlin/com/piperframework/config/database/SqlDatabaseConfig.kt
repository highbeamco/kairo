package com.piperframework.config.database

import com.piperframework.config.ConfigString

/**
 * This class encapsulates the configuration for the connection to an SQL database.
 */
data class SqlDatabaseConfig(
    val jdbcUrl: String,
    val username: String?,
    val password: ConfigString?,
    val connectionTimeout: Long?,
    val minimumIdle: Int?,
    val maximumPoolSize: Int?,
    val properties: Map<String, String> = emptyMap()
)
