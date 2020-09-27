package io.limberapp.typeConversion.conversionService

import io.limberapp.common.validator.Validator
import io.limberapp.typeConversion.TypeConversionService
import io.limberapp.util.uuid.uuidFromBase64Encoded
import java.util.*

object UuidConversionService : TypeConversionService<UUID> {
  override val kClass = UUID::class

  override fun isValid(value: String) = Validator.uuid(value) || Validator.base64EncodedUuid(value)

  override fun fromString(value: String): UUID = when {
    Validator.uuid(value) -> UUID.fromString(value)
    Validator.base64EncodedUuid(value) -> uuidFromBase64Encoded(value)
    else -> error("Invalid UUID $value")
  }

  override fun toString(value: UUID) = value.toString()
}