package kairo.serialization

import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

/**
 * This test is intended to test behaviour strictly related to boolean serialization/deserialization.
 * Therefore, some test cases (such as unknown properties, pretty printing) are not included
 * since they are not strictly related to booleans.
 *
 * Jackson has some default behaviours with is-prefixed properties, where it serializes [isValue] to "value"
 * (dropping the "is" prefix and handling case corrections).
 * The main purpose of this test is to ensure that behaviour is disabled.
 */
internal class BooleanIsObjectMapperTest {
  /**
   * This test is specifically for is-prefixed properties.
   */
  internal data class MyClass(
    val isValue: Boolean,
  )

  private val mapper: JsonMapper = ObjectMapperFactory.builder(ObjectMapperFormat.Json).build()

  @Test
  fun `serialize, false`() {
    mapper.writeValueAsString(MyClass(false)).shouldBe("{\"isValue\":false}")
  }

  @Test
  fun `serialize, true`() {
    mapper.writeValueAsString(MyClass(true)).shouldBe("{\"isValue\":true}")
  }

  @Test
  fun `deserialize, false`() {
    mapper.readValue<MyClass>("{ \"isValue\": false }").shouldBe(MyClass(false))
  }

  @Test
  fun `deserialize, true`() {
    mapper.readValue<MyClass>("{ \"isValue\": true }").shouldBe(MyClass(true))
  }

  @Test
  fun `deserialize, null`() {
    serializationShouldFail {
      mapper.readValue<MyClass>("{ \"isValue\": null }")
    }
  }

  @Test
  fun `deserialize, missing`() {
    serializationShouldFail {
      mapper.readValue<MyClass>("{}")
    }
  }
}