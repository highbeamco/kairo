package io.limberapp.backend.module.orgs.endpoint.org.feature

import com.google.inject.Inject
import com.piperframework.config.serving.ServingConfig
import com.piperframework.endpoint.EndpointConfig
import com.piperframework.endpoint.EndpointConfig.PathTemplateComponent.StringComponent
import com.piperframework.endpoint.EndpointConfig.PathTemplateComponent.VariableComponent
import com.piperframework.endpoint.command.AbstractCommand
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.http.HttpMethod
import io.limberapp.backend.authorization.Authorization
import io.limberapp.backend.endpoint.LimberApiEndpoint
import io.limberapp.backend.module.orgs.service.org.FeatureService
import java.util.UUID

/**
 * Deletes a feature from an org. This does not delete the feature's implementation in the corresponding module. The
 * implementation is not deleted, in case it needs to be recovered.
 */
internal class DeleteFeature @Inject constructor(
    application: Application,
    servingConfig: ServingConfig,
    private val featureService: FeatureService
) : LimberApiEndpoint<DeleteFeature.Command, Unit>(
    application = application,
    pathPrefix = servingConfig.apiPathPrefix,
    endpointConfig = endpointConfig
) {

    internal data class Command(
        val orgId: UUID,
        val featureId: UUID
    ) : AbstractCommand()

    override suspend fun determineCommand(call: ApplicationCall) = Command(
        orgId = call.parameters.getAsType(UUID::class, orgId),
        featureId = call.parameters.getAsType(UUID::class, featureId)
    )

    override suspend fun Handler.handle(command: Command) {
        Authorization.OrgMember(command.orgId).authorize()
        featureService.delete(
            orgId = command.orgId,
            featureId = command.featureId
        )
    }

    companion object {
        const val orgId = "orgId"
        const val featureId = "featureId"
        val endpointConfig = EndpointConfig(
            httpMethod = HttpMethod.Delete,
            pathTemplate = listOf(
                StringComponent("orgs"),
                VariableComponent(orgId),
                StringComponent("features"),
                VariableComponent(featureId)
            )
        )
    }
}