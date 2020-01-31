package io.limberapp.backend.module.forms

import com.piperframework.module.Module
import io.limberapp.backend.module.forms.endpoint.formTemplate.CreateFormTemplate
import io.limberapp.backend.module.forms.endpoint.formTemplate.DeleteFormTemplate
import io.limberapp.backend.module.forms.endpoint.formTemplate.GetFormTemplate
import io.limberapp.backend.module.forms.endpoint.formTemplate.GetFormTemplatesByOrgId
import io.limberapp.backend.module.forms.endpoint.formTemplate.UpdateFormTemplate
import io.limberapp.backend.module.forms.endpoint.formTemplate.question.CreateFormTemplateQuestion
import io.limberapp.backend.module.forms.endpoint.formTemplate.question.DeleteFormTemplateQuestion
import io.limberapp.backend.module.forms.endpoint.formTemplate.question.UpdateFormTemplateQuestion
import io.limberapp.backend.module.forms.service.formTemplate.FormTemplateQuestionService
import io.limberapp.backend.module.forms.service.formTemplate.FormTemplateQuestionServiceImpl
import io.limberapp.backend.module.forms.service.formTemplate.FormTemplateService
import io.limberapp.backend.module.forms.service.formTemplate.FormTemplateServiceImpl
import io.limberapp.backend.module.forms.store.formTemplate.FormTemplateQuestionStore
import io.limberapp.backend.module.forms.store.formTemplate.FormTemplateStore
import io.limberapp.backend.module.forms.store.formTemplate.SqlFormTemplateQuestionStore
import io.limberapp.backend.module.forms.store.formTemplate.SqlFormTemplateStore

class FormsModule : Module() {

    override val endpoints = listOf(

        CreateFormTemplate::class.java,
        DeleteFormTemplate::class.java,
        GetFormTemplate::class.java,
        GetFormTemplatesByOrgId::class.java,
        UpdateFormTemplate::class.java,

        CreateFormTemplateQuestion::class.java,
        DeleteFormTemplateQuestion::class.java,
        UpdateFormTemplateQuestion::class.java
    )

    override fun bindServices() {
        bind(FormTemplateQuestionService::class, FormTemplateQuestionServiceImpl::class)
        bind(FormTemplateService::class, FormTemplateServiceImpl::class)
    }

    override fun bindStores() {
        bind(FormTemplateQuestionStore::class, SqlFormTemplateQuestionStore::class)
        bind(FormTemplateStore::class, SqlFormTemplateStore::class)
    }
}
