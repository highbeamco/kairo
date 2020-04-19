package com.piperframework.serialization

import com.piperframework.dataConversion.DataConversionService
import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.PrimitiveDescriptor
import kotlinx.serialization.PrimitiveKind

/**
 * Abstract serializer that delegates serialization to a data conversion service.
 */
abstract class ConversionServiceSerializer<T : Any>(
    serialName: String,
    private val conversionService: DataConversionService<T>
) : KSerializer<T> {

    override val descriptor = PrimitiveDescriptor(serialName, PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: T) =
        encoder.encodeString(conversionService.toString(value))

    override fun deserialize(decoder: Decoder): T =
        conversionService.fromString(decoder.decodeString())
}