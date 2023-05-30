package limber.util.id

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class RandomIdGeneratorTest {
  private val idGenerator: RandomIdGenerator = RandomIdGenerator(prefix = "id")

  // There's no great way to test randomness, so we just ensure there are no duplicates over 100,000 generations.
  @Test
  fun test() {
    List(100_000) { idGenerator.generate() }.distinct().size.shouldBe(100_000)
  }
}
