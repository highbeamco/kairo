UPDATE auth.feature_role
SET permissions = COALESCE(:permissions, permissions)
WHERE feature_guid = :featureGuid
  AND guid = :featureRoleGuid
RETURNING *