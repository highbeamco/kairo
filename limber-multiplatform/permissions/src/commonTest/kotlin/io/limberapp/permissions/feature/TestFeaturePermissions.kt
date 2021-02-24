package io.limberapp.permissions.feature

import io.limberapp.permissions.Permission
import io.limberapp.permissions.Permissions

internal data class TestFeaturePermissions(
    private val permissions: Set<TestFeaturePermission>,
) : FeaturePermissions() {
  companion object : Permissions.Companion<TestFeaturePermission, TestFeaturePermissions>(
      values = TestFeaturePermission.values(),
  ) {
    override fun fromBooleanList(booleanList: List<Boolean>): TestFeaturePermissions =
        TestFeaturePermissions(from(booleanList))
  }

  override val prefix: Char = 'T'

  override fun contains(permission: Permission): Boolean = permission in permissions

  override fun asBooleanList(): List<Boolean> = values.map { it in permissions }
}