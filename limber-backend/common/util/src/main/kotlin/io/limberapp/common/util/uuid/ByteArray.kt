package io.limberapp.common.util.uuid

import java.nio.ByteBuffer
import java.util.*

const val UUID_BYTES = 16

fun UUID.asByteArray(): ByteArray = ByteBuffer.wrap(ByteArray(UUID_BYTES)).apply {
  putLong(mostSignificantBits)
  putLong(leastSignificantBits)
}.array()

fun uuidFromByteArray(byteArray: ByteArray) = with(ByteBuffer.wrap(byteArray)) { UUID(long, long) }