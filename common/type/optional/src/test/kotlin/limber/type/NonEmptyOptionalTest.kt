package limber.type

import com.fasterxml.jackson.module.kotlin.readValue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test
import java.util.Optional

internal class NonEmptyOptionalTest : OptionalTest() {
  @Test
  override fun `equals method`() {
    OptionalWrapper.Nullable(Optional.of("42")).shouldBe(OptionalWrapper.Nullable(Optional.of("42")))
    OptionalWrapper.Nullable(Optional.of("42")).shouldNotBe(OptionalWrapper.Nullable(null))
    OptionalWrapper.Nullable(Optional.of("42")).shouldNotBe(OptionalWrapper.Nullable(Optional.empty()))
    OptionalWrapper.Nullable(Optional.of("42")).shouldNotBe(OptionalWrapper.Nullable(Optional.of("")))

    OptionalWrapper.NotNullable(Optional.of("42")).shouldBe(OptionalWrapper.NotNullable(Optional.of("42")))
    OptionalWrapper.NotNullable(Optional.of("42")).shouldNotBe(OptionalWrapper.NotNullable(Optional.empty()))
    OptionalWrapper.NotNullable(Optional.of("42")).shouldNotBe(OptionalWrapper.NotNullable(Optional.of("")))
  }

  @Test
  override fun `toString method`() {
    OptionalWrapper.Nullable(Optional.of("42")).toString()
      .shouldBe("Nullable(optional=Optional[42])")

    OptionalWrapper.NotNullable(Optional.of("42")).toString()
      .shouldBe("NotNullable(optional=Optional[42])")
  }

  @Test
  override fun serialize() {
    objectMapper.readValue<OptionalWrapper.Nullable>("{\"optional\":\"42\"}")
      .shouldBe(OptionalWrapper.Nullable(Optional.of("42")))

    objectMapper.readValue<OptionalWrapper.NotNullable>("{\"optional\":\"42\"}")
      .shouldBe(OptionalWrapper.NotNullable(Optional.of("42")))
  }

  @Test
  override fun deserialize() {
    objectMapper.writeValueAsString(OptionalWrapper.Nullable(Optional.of("42")))
      .shouldBe("{\"optional\":\"42\"}")
    objectMapper.writeValueAsString(OptionalWrapper.NotNullable(Optional.of("42")))
      .shouldBe("{\"optional\":\"42\"}")
  }
}