package io.limberapp.backend.module.users.endpoint.user.role

import com.google.inject.Inject
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.http.HttpMethod
import io.limberapp.backend.authorization.Authorization
import io.limberapp.backend.authorization.principal.JwtRole
import io.limberapp.backend.module.users.service.user.UserService
import com.piperframework.config.serving.ServingConfig
import com.piperframework.endpoint.ApiEndpoint
import com.piperframework.endpoint.EndpointConfig
import com.piperframework.endpoint.command.AbstractCommand
import java.util.UUID

/**
 * Revokes a certain role from the user. Roles are system-wide, NOT org-wide.
 */
internal class RemoveUserRole @Inject constructor(
    application: Application,
    servingConfig: com.piperframework.config.serving.ServingConfig,
    private val userService: UserService
) : com.piperframework.endpoint.ApiEndpoint<RemoveUserRole.Command, Unit>(
    application = application,
    pathPrefix = servingConfig.apiPathPrefix,
    endpointConfig = endpointConfig
) {

    internal data class Command(
        val userId: UUID,
        val roleName: JwtRole
    ) : com.piperframework.endpoint.command.AbstractCommand()

    override suspend fun determineCommand(call: ApplicationCall) = Command(
        userId = call.parameters.getAsType(UUID::class, userId),
        roleName = call.parameters.getAsType(JwtRole::class, roleName)
    )

    override fun authorization(command: Command) = Authorization.Superuser

    override suspend fun handler(command: Command) {
        userService.removeRole(command.userId, command.roleName)
    }

    companion object {
        const val userId = "userId"
        const val roleName = "roleName"
        val endpointConfig =
            com.piperframework.endpoint.EndpointConfig(HttpMethod.Delete, "/users/{$userId}/roles/{$roleName}")
    }
}
