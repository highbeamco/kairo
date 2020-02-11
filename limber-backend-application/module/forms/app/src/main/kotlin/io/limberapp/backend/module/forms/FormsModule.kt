package io.limberapp.backend.module.forms

import com.piperframework.module.Module
import io.limberapp.backend.module.forms.endpoint.formInstance.DeleteFormInstance
import io.limberapp.backend.module.forms.endpoint.formInstance.GetFormInstance
import io.limberapp.backend.module.forms.endpoint.formInstance.GetFormInstancesByOrgId
import io.limberapp.backend.module.forms.endpoint.formInstance.PostFormInstance
import io.limberapp.backend.module.forms.endpoint.formInstance.question.DeleteFormInstanceQuestion
import io.limberapp.backend.module.forms.endpoint.formInstance.question.PutFormInstanceQuestion
import io.limberapp.backend.module.forms.endpoint.formTemplate.DeleteFormTemplate
import io.limberapp.backend.module.forms.endpoint.formTemplate.GetFormTemplate
import io.limberapp.backend.module.forms.endpoint.formTemplate.GetFormTemplatesByOrgId
import io.limberapp.backend.module.forms.endpoint.formTemplate.PatchFormTemplate
import io.limberapp.backend.module.forms.endpoint.formTemplate.PostFormTemplate
import io.limberapp.backend.module.forms.endpoint.formTemplate.question.DeleteFormTemplateQuestion
import io.limberapp.backend.module.forms.endpoint.formTemplate.question.PatchFormTemplateQuestion
import io.limberapp.backend.module.forms.endpoint.formTemplate.question.PostFormTemplateQuestion
import io.limberapp.backend.module.forms.service.formInstance.FormInstanceQuestionService
import io.limberapp.backend.module.forms.service.formInstance.FormInstanceQuestionServiceImpl
import io.limberapp.backend.module.forms.service.formInstance.FormInstanceService
import io.limberapp.backend.module.forms.service.formInstance.FormInstanceServiceImpl
import io.limberapp.backend.module.forms.service.formTemplate.FormTemplateQuestionService
import io.limberapp.backend.module.forms.service.formTemplate.FormTemplateQuestionServiceImpl
import io.limberapp.backend.module.forms.service.formTemplate.FormTemplateService
import io.limberapp.backend.module.forms.service.formTemplate.FormTemplateServiceImpl
import io.limberapp.backend.module.forms.store.formInstance.FormInstanceQuestionStore
import io.limberapp.backend.module.forms.store.formInstance.FormInstanceStore
import io.limberapp.backend.module.forms.store.formInstance.SqlFormInstanceMapper
import io.limberapp.backend.module.forms.store.formInstance.SqlFormInstanceQuestionStore
import io.limberapp.backend.module.forms.store.formInstance.SqlFormInstanceStore
import io.limberapp.backend.module.forms.store.formTemplate.FormTemplateQuestionStore
import io.limberapp.backend.module.forms.store.formTemplate.FormTemplateStore
import io.limberapp.backend.module.forms.store.formTemplate.SqlFormTemplateMapper
import io.limberapp.backend.module.forms.store.formTemplate.SqlFormTemplateQuestionStore
import io.limberapp.backend.module.forms.store.formTemplate.SqlFormTemplateStore

class FormsModule : Module() {

    private val formTemplateEndpoints = listOf(

        PostFormTemplate::class.java,
        GetFormTemplate::class.java,
        GetFormTemplatesByOrgId::class.java,
        PatchFormTemplate::class.java,
        DeleteFormTemplate::class.java,

        PostFormTemplateQuestion::class.java,
        PatchFormTemplateQuestion::class.java,
        DeleteFormTemplateQuestion::class.java
    )

    private val formInstanceEndpoints = listOf(

        PostFormInstance::class.java,
        GetFormInstance::class.java,
        GetFormInstancesByOrgId::class.java,
        DeleteFormInstance::class.java,

        PutFormInstanceQuestion::class.java,
        DeleteFormInstanceQuestion::class.java
    )

    override val endpoints = formTemplateEndpoints + formInstanceEndpoints

    override fun bindServices() {
        bindFormTemplateServices()
        bindFormInstanceServices()
    }

    private fun bindFormTemplateServices() {
        bind(FormTemplateService::class, FormTemplateServiceImpl::class)
        bind(FormTemplateQuestionService::class, FormTemplateQuestionServiceImpl::class)
    }

    private fun bindFormInstanceServices() {
        bind(FormInstanceService::class, FormInstanceServiceImpl::class)
        bind(FormInstanceQuestionService::class, FormInstanceQuestionServiceImpl::class)
    }

    override fun bindStores() {
        bindFormTemplateStores()
        bindFormInstanceStores()
    }

    private fun bindFormTemplateStores() {
        bind(FormTemplateStore::class, SqlFormTemplateStore::class)
        bind(FormTemplateQuestionStore::class, SqlFormTemplateQuestionStore::class)
    }

    private fun bindFormInstanceStores() {
        bind(FormInstanceStore::class, SqlFormInstanceStore::class)
        bind(FormInstanceQuestionStore::class, SqlFormInstanceQuestionStore::class)
    }
}
