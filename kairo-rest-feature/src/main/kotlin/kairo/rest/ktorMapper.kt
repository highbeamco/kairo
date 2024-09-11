package kairo.rest

import com.fasterxml.jackson.databind.json.JsonMapper
import kairo.serialization.ObjectMapperFactory
import kairo.serialization.ObjectMapperFormat

internal val ktorMapper: JsonMapper =
  ObjectMapperFactory.builder(ObjectMapperFormat.Json) {
    prettyPrint = true
  }.build()