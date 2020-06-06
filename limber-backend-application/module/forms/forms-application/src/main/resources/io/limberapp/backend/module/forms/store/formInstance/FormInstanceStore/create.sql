INSERT INTO forms.form_instance (guid, created_date, feature_guid, form_template_guid,
                                 number,
                                 creator_account_guid)
VALUES (:guid, :createdDate, :featureGuid, :formTemplateGuid,
        (SELECT COALESCE(MAX(number), 0) + 1 FROM forms.form_instance WHERE feature_guid = :featureGuid),
        :creatorAccountGuid)
RETURNING *
