package com.piperframework.store

import com.piperframework.sql.columnTypes.localDateTime.localdatetime
import org.jetbrains.exposed.dao.IdTable

abstract class SqlTable(schema: String, tableName: String) : IdTable<Long>() {

    final override val tableName = "$schema.$tableName"

    final override val id = long("id").entityId()
    val createdDate = localdatetime("created_date")
}