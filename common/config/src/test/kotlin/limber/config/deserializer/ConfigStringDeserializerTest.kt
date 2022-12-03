package limber.config.deserializer

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.module.kotlin.convertValue
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import limber.serialization.ObjectMapperFactory
import org.junit.jupiter.api.Test

internal class ConfigStringDeserializerTest {
  private val objectMapper: ObjectMapper = ObjectMapperFactory.builder(ObjectMapperFactory.Format.Json).build()

  data class Config(
    @JsonDeserialize(using = ConfigStringDeserializer::class)
    val someValue: String?,
  )

  init {
    CommandSource.withOverride({ " " }) {}
    EnvironmentVariableSource.withOverride({ " " }) {}
    GcpSecretSource.withOverride({ " " }) {}
  }

  @Test
  fun `plaintext - value is set`() {
    val map = mapOf("someValue" to "the value")
    objectMapper.convertValue<Config>(map).someValue.shouldBe("the value")
  }

  @Test
  fun `plaintext - value is null`() {
    val map = mapOf("someValue" to null)
    objectMapper.convertValue<Config>(map).someValue.shouldBeNull()
  }

  @Test
  fun `plaintext - value not set`() {
    val map = emptyMap<String, String>()
    objectMapper.convertValue<Config>(map).someValue.shouldBeNull()
  }

  @Test
  fun `environment variable - value is set`() {
    EnvironmentVariableSource.withOverride({ "val from env" }) {
      val map = mapOf(
        "someValue" to mapOf(
          "type" to "EnvironmentVariable",
          "name" to "TEST_ENV_VAR",
        ),
      )
      objectMapper.convertValue<Config>(map).someValue.shouldBe("val from env")
    }
  }

  @Test
  fun `environment variable - value not set`() {
    EnvironmentVariableSource.withOverride({ null }) {
      val map = mapOf(
        "someValue" to mapOf(
          "type" to "EnvironmentVariable",
          "name" to "TEST_ENV_VAR",
        ),
      )
      objectMapper.convertValue<Config>(map).someValue.shouldBeNull()
    }
  }

  @Test
  fun `gcp secret`() {
    val map = mapOf(
      "someValue" to mapOf(
        "type" to "GcpSecret",
        "name" to "TEST_ENV_VAR",
      ),
    )
    shouldThrow<IllegalArgumentException> { objectMapper.convertValue<Config>(map) }
  }

  @Test
  fun command() {
    val map = mapOf(
      "someValue" to mapOf(
        "type" to "Command",
        "name" to "echo \"val from cmd\"",
      ),
    )
    shouldThrow<IllegalArgumentException> { objectMapper.convertValue<Config>(map) }
  }
}
