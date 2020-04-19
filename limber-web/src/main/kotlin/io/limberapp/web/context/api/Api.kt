package io.limberapp.web.context.api

import io.limberapp.web.api.Fetch
import io.limberapp.web.api.formInstance.FormInstanceApi
import io.limberapp.web.api.formTemplate.FormTemplateApi
import io.limberapp.web.api.org.OrgApi
import io.limberapp.web.api.tenant.TenantApi
import io.limberapp.web.api.user.UserApi

internal class Api(fetch: Fetch) {
    val formInstances = FormInstanceApi(fetch)
    val formTemplates = FormTemplateApi(fetch)
    val orgs = OrgApi(fetch)
    val tenants = TenantApi(fetch)
    val users = UserApi(fetch)
}