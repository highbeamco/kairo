package io.limberapp.backend.module.orgs

import com.piperframework.module.Module
import com.piperframework.serialization.Json
import io.limberapp.backend.module.orgs.endpoint.org.DeleteOrg
import io.limberapp.backend.module.orgs.endpoint.org.GetOrg
import io.limberapp.backend.module.orgs.endpoint.org.PatchOrg
import io.limberapp.backend.module.orgs.endpoint.org.PostOrg
import io.limberapp.backend.module.orgs.endpoint.org.feature.DeleteFeature
import io.limberapp.backend.module.orgs.endpoint.org.feature.PatchFeature
import io.limberapp.backend.module.orgs.endpoint.org.feature.PostFeature
import io.limberapp.backend.module.orgs.service.org.FeatureService
import io.limberapp.backend.module.orgs.service.org.FeatureServiceImpl
import io.limberapp.backend.module.orgs.service.org.OrgService
import io.limberapp.backend.module.orgs.service.org.OrgServiceImpl
import io.limberapp.backend.module.orgs.store.org.FeatureStore
import io.limberapp.backend.module.orgs.store.org.OrgStore
import io.limberapp.backend.module.orgs.store.org.SqlFeatureStore
import io.limberapp.backend.module.orgs.store.org.SqlOrgMapper
import io.limberapp.backend.module.orgs.store.org.SqlOrgMapperImpl
import io.limberapp.backend.module.orgs.store.org.SqlOrgStore

class OrgsModule : Module() {

    override fun configureJson(json: Json) = Unit

    override val endpoints = listOf(

        PostOrg::class.java,
        GetOrg::class.java,
        PatchOrg::class.java,
        DeleteOrg::class.java,

        PostFeature::class.java,
        PatchFeature::class.java,
        DeleteFeature::class.java
    )

    override fun bindServices() {
        bind(OrgService::class, OrgServiceImpl::class)
        bind(FeatureService::class, FeatureServiceImpl::class)
    }

    override fun bindStores() {
        bind(SqlOrgMapper::class, SqlOrgMapperImpl::class)
        bind(OrgStore::class, SqlOrgStore::class)
        bind(FeatureStore::class, SqlFeatureStore::class)
    }
}