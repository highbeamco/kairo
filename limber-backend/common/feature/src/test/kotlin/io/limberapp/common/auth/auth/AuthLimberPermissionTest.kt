package io.limberapp.common.auth.auth

import io.limberapp.common.auth.jwt.Jwt
import io.limberapp.common.permissions.limberPermissions.LimberPermission
import io.limberapp.common.permissions.limberPermissions.LimberPermissions
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class AuthLimberPermissionTest {
  @Test
  fun `No JWT`() {
    val requiredOrgGuid = UUID.randomUUID()
    val result = AuthOrgMember(requiredOrgGuid)
        .authorize(null)
    assertFalse(result)
  }

  @Test
  fun `Missing Limber permission`() {
    val jwt = Jwt(
        permissions = LimberPermissions.fromBitString("01"),
        org = null,
        features = null,
        user = null,
    )
    val result = AuthLimberPermission(permission = LimberPermission.IDENTITY_PROVIDER)
        .authorize(jwt)
    assertFalse(result)
  }

  @Test
  fun `Has Limber permission (happy path)`() {
    val jwt = Jwt(
        permissions = LimberPermissions.fromBitString("11"),
        org = null,
        features = null,
        user = null,
    )
    val result = AuthLimberPermission(permission = LimberPermission.IDENTITY_PROVIDER)
        .authorize(jwt)
    assertTrue(result)
  }
}
