UPDATE orgs.org
SET name = COALESCE(:name, name)
WHERE guid = :orgGuid
  AND archived_date IS NULL
