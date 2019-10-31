package io.limberapp.backend

import com.google.inject.Provides
import com.google.inject.Singleton
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoDatabase
import io.ktor.application.Application
import io.limberapp.framework.MainModule
import io.limberapp.framework.config.Config
import io.limberapp.framework.createClient

internal class MainModuleImpl(
    application: Application,
    config: Config
) : MainModule(application, config) {

    @Provides
    @Singleton
    fun mongoClient(config: Config) = config.createClient()

    @Provides
    @Singleton
    fun mongoDatabase(config: Config, mongoClient: MongoClient): MongoDatabase =
        mongoClient.getDatabase(config.database.database)
}
