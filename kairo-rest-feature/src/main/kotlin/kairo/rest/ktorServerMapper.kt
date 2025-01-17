package kairo.rest

import com.fasterxml.jackson.databind.json.JsonMapper
import kairo.serialization.ObjectMapperFactory
import kairo.serialization.ObjectMapperFormat
import kairo.serialization.module.primitives.TrimWhitespace

public val ktorServerMapper: JsonMapper =
  ObjectMapperFactory.builder(ObjectMapperFormat.Json) {
    prettyPrint = true
    trimWhitespace = TrimWhitespace.Type.TrimBoth
  }.build()
