DELETE
FROM auth.org_role_membership
WHERE org_role_guid = :orgRoleGuid
  AND user_guid = :userGuid
