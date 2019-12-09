package com.piperframework.mongo

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.piperframework.util.uuid.uuidFromByteArray
import java.util.UUID

class BsonUuidDeserializer : JsonDeserializer<UUID>() {

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext) = uuidFromByteArray(p.binaryValue)
}