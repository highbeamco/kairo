package com.piperframework

import com.fasterxml.jackson.databind.ObjectMapper
import com.piperframework.config.Config
import kotlin.reflect.KClass

/**
 * Loads the application configuration from a resource on the application classpath.
 */
abstract class PiperConfigLoader<C : Config>(private val klass: KClass<C>) {

    /**
     * Abstract to allow for non-JSON data formats such as YAML.
     */
    protected abstract val objectMapper: ObjectMapper

    abstract fun load(): C

    protected fun loadInternal(fileName: String): C? {
        val stream = this.javaClass.getResourceAsStream(fileName) ?: return null
        return objectMapper.readValue(stream, klass.java)
    }
}