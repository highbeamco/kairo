package io.limberapp.backend.module.auth.endpoint.jwtCliamsRequest

import com.google.inject.Inject
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.http.HttpMethod
import io.limberapp.backend.module.auth.mapper.api.jwtClaimsRequest.JwtClaimsRequestMapper
import io.limberapp.backend.module.auth.rep.jwtClaimsRequest.JwtClaimsRequestRep
import io.limberapp.backend.module.auth.service.jwtClaimsRequest.JwtClaimsRequestService
import io.limberapp.framework.config.serving.ServingConfig
import io.limberapp.framework.endpoint.ApiEndpoint
import io.limberapp.framework.endpoint.EndpointConfig
import io.limberapp.framework.endpoint.authorization.Authorization
import io.limberapp.framework.endpoint.command.AbstractCommand

/**
 * Creates something called a "JWT claims request", which is never persisted anywhere. Instead,
 * think of this claims request as a transitive request that is resolved when the HTTP request is
 * resolved. The result of the JWT claims request is an object containing JWT claims that should be
 * included in JWTs. Auth0 creates JWT claims requests and uses the results every time it issues a
 * JWT.
 */
internal class CreateJwtClaimsRequest @Inject constructor(
    application: Application,
    servincConfig: ServingConfig,
    private val jwtClaimsRequestService: JwtClaimsRequestService,
    private val jwtClaimsRequestMapper: JwtClaimsRequestMapper
) : ApiEndpoint<CreateJwtClaimsRequest.Command, JwtClaimsRequestRep.Complete>(
    application = application,
    pathPrefix = servincConfig.apiPathPrefix,
    endpointConfig = endpointConfig
) {

    internal data class Command(
        val creationRep: JwtClaimsRequestRep.Creation
    ) : AbstractCommand()

    override suspend fun determineCommand(call: ApplicationCall) = Command(
        creationRep = call.getAndValidateBody()
    )

    // TODO: This should not be public
    override fun authorization(command: Command) = Authorization.Public

    override suspend fun handler(command: Command): JwtClaimsRequestRep.Complete {
        val creationModel = jwtClaimsRequestMapper.creationModel(command.creationRep)
        val completeModel = jwtClaimsRequestService.requestJwtClaims(creationModel)
        return jwtClaimsRequestMapper.completeRep(completeModel)
    }

    companion object {
        val endpointConfig = EndpointConfig(HttpMethod.Post, "/jwt-claims-request")
    }
}
