package io.limberapp.backend.module.orgs.endpoint.org.feature

import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.http.HttpStatusCode
import io.limberapp.backend.module.orgs.endpoint.org.CreateOrg
import io.limberapp.backend.module.orgs.endpoint.org.GetOrg
import io.limberapp.backend.module.orgs.mapper.api.org.DEFAULT_FEATURE_CREATION_REP
import io.limberapp.backend.module.orgs.model.org.FeatureModel
import io.limberapp.backend.module.orgs.rep.feature.FeatureRep
import io.limberapp.backend.module.orgs.rep.org.OrgRep
import io.limberapp.backend.module.orgs.testing.ResourceTest
import org.junit.Test
import java.time.LocalDateTime
import java.util.UUID
import kotlin.test.assertEquals

internal class DeleteFeatureTest : ResourceTest() {

    @Test
    fun orgDoesNotExist() {
        piperTest.test(
            endpointConfig = DeleteFeature.endpointConfig,
            pathParams = mapOf(
                DeleteFeature.orgId to UUID.randomUUID().toString(),
                DeleteFeature.featureId to UUID.randomUUID().toString()
            ),
            expectedStatusCode = HttpStatusCode.NotFound
        ) {}
    }

    @Test
    fun featureDoesNotExist() {

        val orgCreationRep = OrgRep.Creation("Cranky Pasta")
        val orgId = deterministicUuidGenerator[0]
        piperTest.test(
            endpointConfig = CreateOrg.endpointConfig,
            body = orgCreationRep
        ) {}

        piperTest.test(
            endpointConfig = DeleteFeature.endpointConfig,
            pathParams = mapOf(
                DeleteFeature.orgId to orgId.toString(),
                DeleteFeature.featureId to UUID.randomUUID().toString()
            ),
            expectedStatusCode = HttpStatusCode.NotFound
        ) {}
    }

    @Test
    fun exists() {

        val orgCreationRep = OrgRep.Creation("Cranky Pasta")
        val orgId = deterministicUuidGenerator[0]
        val defaultFeatureId = deterministicUuidGenerator[1]
        val featureId = deterministicUuidGenerator[2]
        piperTest.test(
            endpointConfig = CreateOrg.endpointConfig,
            body = orgCreationRep
        ) {}

        val featureCreationRep = FeatureRep.Creation("Events", "/events", FeatureModel.Type.HOME)
        piperTest.test(
            endpointConfig = CreateFeature.endpointConfig,
            pathParams = mapOf(CreateFeature.orgId to orgId.toString()),
            body = featureCreationRep
        ) {}

        piperTest.test(
            endpointConfig = DeleteFeature.endpointConfig,
            pathParams = mapOf(
                DeleteFeature.orgId to orgId.toString(),
                DeleteFeature.featureId to featureId.toString()
            )
        ) {}

        piperTest.test(
            endpointConfig = GetOrg.endpointConfig,
            pathParams = mapOf("orgId" to orgId.toString())
        ) {
            val actual = objectMapper.readValue<OrgRep.Complete>(response.content!!)
            val defaultFeature = FeatureRep.Complete(
                id = defaultFeatureId,
                created = LocalDateTime.now(fixedClock),
                name = DEFAULT_FEATURE_CREATION_REP.name,
                path = DEFAULT_FEATURE_CREATION_REP.path,
                type = DEFAULT_FEATURE_CREATION_REP.type
            )
            val expected = OrgRep.Complete(
                id = orgId,
                created = LocalDateTime.now(fixedClock),
                name = orgCreationRep.name,
                features = listOf(defaultFeature),
                members = emptyList()
            )
            assertEquals(expected, actual)
        }
    }
}
