package io.limberapp.backend.module.auth.endpoint.personalAccessToken

import com.google.inject.Inject
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.http.HttpMethod
import io.limberapp.backend.module.auth.mapper.api.personalAccessToken.PersonalAccessTokenMapper
import io.limberapp.backend.module.auth.rep.personalAccessToken.PersonalAccessTokenRep
import io.limberapp.backend.module.auth.service.personalAccessToken.PersonalAccessTokenService
import io.limberapp.framework.config.serving.ServingConfig
import io.limberapp.framework.endpoint.ApiEndpoint
import io.limberapp.framework.endpoint.EndpointConfig
import io.limberapp.framework.endpoint.authorization.Authorization
import io.limberapp.framework.endpoint.command.AbstractCommand
import java.util.UUID

/**
 * Returns all personal access tokens for the given user. Note that this endpoint does not actually
 * return the token itself, just its ID. The token itself is only returned once, immediately after
 * it is created. The user must record the token appropriately, because it cannot be returned again.
 * If a new token is needed, the user can always delete the existing token and create another.
 */
internal class GetPersonalAccessTokensByUserId @Inject constructor(
    application: Application,
    servingConfig: ServingConfig,
    private val personalAccessTokenService: PersonalAccessTokenService,
    private val personalAccessTokenMapper: PersonalAccessTokenMapper
) : ApiEndpoint<GetPersonalAccessTokensByUserId.Command, List<PersonalAccessTokenRep.Complete>>(
    application = application,
    pathPrefix = servingConfig.apiPathPrefix,
    endpointConfig = endpointConfig
) {

    internal data class Command(
        val userId: UUID
    ) : AbstractCommand()

    override suspend fun determineCommand(call: ApplicationCall) = Command(
        userId = call.parameters.getAsType(UUID::class, userId)
    )

    override fun authorization(command: Command) = Authorization.Superuser

    override suspend fun handler(command: Command): List<PersonalAccessTokenRep.Complete> {
        val models = personalAccessTokenService.getByUserId(command.userId)
        return models.map { personalAccessTokenMapper.completeRep(it) }
    }

    companion object {
        const val userId = "userId"
        val endpointConfig = EndpointConfig(
            httpMethod = HttpMethod.Get,
            pathTemplate = "/users/{$userId}/personal-access-tokens"
        )
    }
}