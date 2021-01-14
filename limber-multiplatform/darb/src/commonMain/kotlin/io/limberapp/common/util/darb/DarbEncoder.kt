package io.limberapp.common.util.darb

import io.limberapp.common.logging.LoggerFactory

/**
 * Converts between boolean lists and the DARB string format.
 */
object DarbEncoder {
  private val logger = LoggerFactory.getLogger(DarbEncoder::class)

  private const val CHUNK_SIZE = 4 // Warning, changing this alone will break the code.
  private val HEX = Regex("^[A-Fa-f0-9]*$")

  fun encode(booleanList: List<Boolean>): String {
    val bitString = BitStringEncoder.encode(booleanList)
    logger.debug("Encoding bit string: $bitString.")

    // Chunk by 4 because each hex represents 4 bits.
    val chunkedBooleanList = booleanList.chunked(CHUNK_SIZE)

    // Map each chunk of 4 bits to a hex character, then join the characters together.
    val hex = chunkedBooleanList.joinToString("") {
      // intValue is the value of the hex character from 0 to 15 (inclusive).
      var intValue = 0
      if (it.getOrNull(0) == true) intValue += 8
      if (it.getOrNull(1) == true) intValue += 4
      if (it.getOrNull(2) == true) intValue += 2
      if (it.getOrNull(3) == true) intValue += 1

      return@joinToString if (intValue < 10) {
        // 0 through 9 are represented by digits.
        ('0' + intValue)
      } else {
        // 10 through 15 are represented by capital letters A through F.
        ('A' + (intValue - 10))
      }.toString()
    }

    // DARB prefixes the hex characters with the size of the length of the data.
    val size = booleanList.size
    return "$size.$hex".also {
      logger.debug("Encoded bit string $bitString to $it.")
    }
  }

  fun decode(darb: String): List<Boolean> {
    logger.debug("Decoding DARB: $darb.")
    val (size, hex) = getComponentsOrNull(darb) ?: error("Invalid DARB: $darb")

    // Map each hex to a list of 4 digits representing the hex in binary.
    val booleanList = hex.flatMap {
      // intValue is the value of the hex character from 0 to 15 (inclusive).
      val intValue = when (it) {
        // 0 through 9 are represented by digits.
        in CharRange('0', '9') -> it.toInt() - '0'.toInt()
        // 10 through 15 are represented by capital letters A through F.
        in CharRange('A', 'F') -> it.toInt() - 'A'.toInt() + 10
        in CharRange('a', 'f') -> it.toInt() - 'a'.toInt() + 10
        // No other characters are supported.
        else -> throw IllegalArgumentException()
      }

      return@flatMap listOf(
          intValue >= 8, // first bit
          intValue % 8 >= 4, // second bit
          intValue % 4 >= 2, // third bit
          intValue % 2 >= 1, // fourth bit
      )
    }

    // Take the size into account to return a list of the correct length.
    // This will omit between 0 and 3 booleans from the end.
    return booleanList.subList(0, size).also {
      val bitString = BitStringEncoder.encode(it)
      logger.debug("Decoded DARB $darb to $bitString.")
    }
  }

  /**
   * Returns the size and the hex as a pair, or null if the input is invalid.
   */
  fun getComponentsOrNull(darb: String): Pair<Int, String>? {
    // DARB always has 2 components separated by a dot, and no dots elsewhere in the syntax.
    val components = darb.split('.')
    if (components.size != 2) return null

    // The first component is the size (positive).
    val size = components[0].toInt()
    if (size < 0) return null

    // The second component is the hex, the length of which must correlate with the size.
    val hex = components[1]
    if (hex.length != (size + CHUNK_SIZE - 1) / CHUNK_SIZE) return null // This math works due to integer rounding.
    if (!HEX.matches(hex)) return null
    return Pair(size, hex)
  }
}
