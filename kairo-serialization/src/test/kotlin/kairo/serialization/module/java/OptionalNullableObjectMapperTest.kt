package kairo.serialization.module.java

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.kotest.matchers.shouldBe
import java.util.Optional
import kairo.serialization.ObjectMapperFactory
import kairo.serialization.ObjectMapperFormat
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

/**
 * This test is intended to test behaviour strictly related to optional serialization/deserialization.
 * Therefore, some test cases (such as unknown properties, pretty printing) are not included
 * since they are not strictly related to optionals.
 *
 * The behaviour here might seem a bit odd at first,
 * since null is serialized to missing and Optional.empty() is serialized to null.
 * However, this approach is in fact necessary to provide consistency between nullable and non-nullable Optionals.
 */
internal class OptionalNullableObjectMapperTest {
  /**
   * This test is specifically for nullable properties.
   */
  internal data class MyClass(
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val value: Optional<Int>?,
  )

  private val mapper: JsonMapper = ObjectMapperFactory.builder(ObjectMapperFormat.Json).build()

  @Test
  fun `serialize, present`(): Unit = runTest {
    mapper.writeValueAsString(MyClass(Optional.of(42))).shouldBe("{\"value\":42}")
  }

  @Test
  fun `serialize, empty`(): Unit = runTest {
    mapper.writeValueAsString(MyClass(Optional.empty())).shouldBe("{\"value\":null}")
  }

  @Test
  fun `serialize, null`(): Unit = runTest {
    mapper.writeValueAsString(MyClass(null)).shouldBe("{}")
  }

  @Test
  fun `deserialize, present`(): Unit = runTest {
    mapper.readValue<MyClass>("{ \"value\": 42 }").shouldBe(MyClass(Optional.of(42)))
  }

  @Test
  fun `deserialize, null`(): Unit = runTest {
    mapper.readValue<MyClass>("{ \"value\": null }").shouldBe(MyClass(Optional.empty()))
  }

  @Test
  fun `deserialize, missing`(): Unit = runTest {
    mapper.readValue<MyClass>("{}").shouldBe(MyClass(null))
  }
}