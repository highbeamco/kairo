package kairo.protectedString

import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.kotest.matchers.shouldBe
import kairo.serialization.ObjectMapperFactory
import kairo.serialization.ObjectMapperFormat
import org.junit.jupiter.api.Test

internal class ProtectedStringSerializationTest {
  private val mapper: JsonMapper = ObjectMapperFactory.builder(ObjectMapperFormat.Json).build()

  @Test
  fun serialize() {
    mapper.writeValueAsString(ProtectedString("1")).shouldBe("\"1\"")
  }

  @Test
  fun deserialize() {
    mapper.readValue<ProtectedString>("\"1\"").shouldBe(ProtectedString("1"))
  }
}