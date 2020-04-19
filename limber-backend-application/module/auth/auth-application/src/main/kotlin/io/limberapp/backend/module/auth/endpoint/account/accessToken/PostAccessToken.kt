package io.limberapp.backend.module.auth.endpoint.account.accessToken

import com.google.inject.Inject
import com.piperframework.config.serving.ServingConfig
import com.piperframework.restInterface.template
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.limberapp.backend.authorization.Authorization
import io.limberapp.backend.authorization.principal.JwtRole
import io.limberapp.backend.endpoint.LimberApiEndpoint
import io.limberapp.backend.module.auth.api.accessToken.AccessTokenApi
import io.limberapp.backend.module.auth.mapper.accessToken.AccessTokenMapper
import io.limberapp.backend.module.auth.rep.accessToken.AccessTokenRep
import io.limberapp.backend.module.auth.service.accessToken.AccessTokenService
import java.util.UUID

/**
 * Creates an access token for the given account. Note that this endpoint returns a "one time use" rep. This means that
 * the token itself will only be returned by this endpoint, immediately after it is created. The account must record the
 * token appropriately, because it cannot be returned again. If a new token is needed, the account can always delete the
 * existing token and create another.
 */
internal class PostAccessToken @Inject constructor(
    application: Application,
    servingConfig: ServingConfig,
    private val accessTokenService: AccessTokenService,
    private val accessTokenMapper: AccessTokenMapper
) : LimberApiEndpoint<AccessTokenApi.Post, AccessTokenRep.OneTimeUse>(
    application, servingConfig.apiPathPrefix,
    endpointTemplate = AccessTokenApi.Post::class.template()
) {

    override suspend fun determineCommand(call: ApplicationCall) = AccessTokenApi.Post(
        accountId = call.parameters.getAsType(UUID::class, "accountId")
    )

    override suspend fun Handler.handle(command: AccessTokenApi.Post): AccessTokenRep.OneTimeUse {
        Authorization.Role(JwtRole.SUPERUSER).authorize()
        val (model, rawSecretAsUuid) = accessTokenMapper.model(command.accountId)
        accessTokenService.create(model)
        return accessTokenMapper.oneTimeUseRep(model, rawSecretAsUuid)
    }
}
