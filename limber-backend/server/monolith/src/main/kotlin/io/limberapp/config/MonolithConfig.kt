package io.limberapp.config

data class MonolithConfig(
    override val authentication: AuthenticationConfig,
    override val clock: ClockConfig,
    val hosts: MonolithHosts,
    val sqlDatabase: SqlDatabaseConfig,
    override val uuids: UuidsConfig,
) : Config
