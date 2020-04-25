package io.limberapp.backend.adhoc

import com.piperframework.module.SqlWrapper
import io.limberapp.backend.adhoc.helper.dbConfig

fun main(args: Array<String>) {
    val config = dbConfig(args.single())
    with(SqlWrapper(config)) {
        connect()
        dbReset()
        runMigrations()
        disconnect()
    }
}

internal fun SqlWrapper.dbReset() {
    with(checkNotNull(dataSource).connection) {
        createStatement().execute("DROP SCHEMA IF EXISTS auth, forms, orgs, users CASCADE")
        createStatement().execute("DROP TABLE IF EXISTS flyway_schema_history")
    }
}