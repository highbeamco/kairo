package com.piperframework.jackson.objectMapper

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.piperframework.dataConversion.conversionService.UuidConversionService
import com.piperframework.jackson.module.conversionService.ConversionServiceModule

/**
 * Custom ObjectMapper configured for Kotlin, pretty printing, custom datatype conversion, etc. This should be used
 * across Piper.
 */
open class PiperObjectMapper(
    jsonFactory: JsonFactory? = null,
    prettyPrint: Boolean = false,
    serializeUnitToEmptyString: Boolean = false
) : ObjectMapper(jsonFactory) {

    init {
        registerKotlinModule(serializeUnitToEmptyString)
        if (prettyPrint) configurePrettyPrinting()
        ignoreUnknownProperties()
        registerDefaultModules()
    }

    private fun registerKotlinModule(serializeUnitToEmptyString: Boolean) {
        val kotlinModule = KotlinModule()
        if (serializeUnitToEmptyString) {
            kotlinModule.addSerializer(object : StdSerializer<Unit>(Unit::class.java) {
                override fun serialize(value: Unit, gen: JsonGenerator, provider: SerializerProvider) = Unit
            })
        }
        registerModule(kotlinModule)
    }

    private fun configurePrettyPrinting() {
        setDefaultPrettyPrinter(DefaultPrettyPrinter())
    }

    private fun ignoreUnknownProperties() {
        disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    }

    private fun registerDefaultModules() {

        registerModule(JavaTimeModule())
        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)

        registerModule(ConversionServiceModule(UuidConversionService()))
    }
}
