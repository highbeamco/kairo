package io.limberapp.permissions.featurePermissions.feature.forms

import io.limberapp.common.util.darb.BitStringEncoder
import io.limberapp.permissions.featurePermissions.FeaturePermissions

/**
 * All forms feature permissions, in the correct order, with a quick sanity check on the digits.
 */
private val ALL_FORMS_FEATURE_PERMISSIONS = with(FormsFeaturePermission.values()) {
  sortedBy { it.bit }
      .apply { check(this@apply.map { it.bit } == this@with.indices.map { it }) }
}

internal const val FORMS_FEATURE_PREFIX = 'F'

data class FormsFeaturePermissions(override val permissions: Set<FormsFeaturePermission>) : FeaturePermissions() {
  override val prefix = FORMS_FEATURE_PREFIX

  override fun allPermissions() = ALL_FORMS_FEATURE_PERMISSIONS

  companion object {
    fun none() = FormsFeaturePermissions(emptySet())

    fun fromBitString(bitString: String) = fromBooleanList(BitStringEncoder.decode(bitString))

    internal fun fromBooleanList(booleanList: List<Boolean>) = FormsFeaturePermissions(
        permissions = ALL_FORMS_FEATURE_PERMISSIONS
            .filterIndexed { i, _ -> booleanList.getOrNull(i) == true }
            .toSet()
    )

    fun Collection<FormsFeaturePermissions>.union() = FormsFeaturePermissions(
        permissions = fold(emptySet()) { acc, permissions -> acc.union(permissions.permissions) }
    )
  }
}
