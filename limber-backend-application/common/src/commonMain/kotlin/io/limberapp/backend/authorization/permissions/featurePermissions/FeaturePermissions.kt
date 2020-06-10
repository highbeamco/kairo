package io.limberapp.backend.authorization.permissions.featurePermissions

import com.piperframework.util.darb.BitStringEncoder
import com.piperframework.util.darb.DarbEncoder
import io.limberapp.backend.authorization.permissions.Permissions
import io.limberapp.backend.authorization.permissions.featurePermissions.feature.forms.FORMS_FEATURE_PREFIX
import io.limberapp.backend.authorization.permissions.featurePermissions.feature.forms.FormsFeaturePermissions
import io.limberapp.backend.authorization.permissions.featurePermissions.feature.home.HOME_FEATURE_PREFIX
import io.limberapp.backend.authorization.permissions.featurePermissions.feature.home.HomeFeaturePermissions
import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.PrimitiveDescriptor
import kotlinx.serialization.PrimitiveKind
import kotlinx.serialization.Serializable

/**
 * Permissions that only apply to a specific organization feature.
 */
@Serializable(with = FeaturePermissionsSerializer::class)
abstract class FeaturePermissions : Permissions<FeaturePermission>() {
  companion object {
    fun fromDarb(darb: String) = darb.split('.', limit = 2).let {
      return@let fromBooleanList(it[0].single(), DarbEncoder.decode(it[1]))
    }

    fun fromBitString(bitString: String) = bitString.let {
      return@let fromBooleanList(it.first(), BitStringEncoder.decode(it.drop(1)))
    }

    private fun fromBooleanList(prefix: Char, booleanList: List<Boolean>) = when (prefix) {
      HOME_FEATURE_PREFIX -> HomeFeaturePermissions.fromBooleanList(booleanList)
      FORMS_FEATURE_PREFIX -> FormsFeaturePermissions.fromBooleanList(booleanList)
      else -> error("Unrecognized feature permissions prefix: $prefix.")
    }
  }
}

object FeaturePermissionsSerializer : KSerializer<FeaturePermissions> {
  override val descriptor = PrimitiveDescriptor("FeaturePermissions", PrimitiveKind.STRING)

  override fun serialize(encoder: Encoder, value: FeaturePermissions) = encoder.encodeString(value.asDarb())

  override fun deserialize(decoder: Decoder) = FeaturePermissions.fromDarb(decoder.decodeString())
}
